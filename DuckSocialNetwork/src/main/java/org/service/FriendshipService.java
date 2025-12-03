package org.service;

import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.users.duck.Duck;
import org.domain.users.relationships.Friendship;
import org.domain.users.User;
import org.domain.exceptions.ServiceException;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.Repository;
import org.repository.util.paging.Page;
import org.service.utils.IdGenerator;

import java.util.*;
import java.util.stream.StreamSupport;

public class FriendshipService extends EntityService<Long, Friendship>{


    private UsersService usersService;

    Map<Long,Set<Long>> friendshipNetwork;

    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }


    public FriendshipService(Validator<Friendship> validator, PagingRepository<Long, Friendship> repository, IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);

    }

    @Override
    public Friendship save(Friendship friendship) {

        validator.validate(friendship);
        friendship.setId(idGenerator.nextId());

        friendship.getUser1().addFriend(friendship.getUser2());
        friendshipNetwork.clear();

        return repository.save(friendship);
    }


    @Override
    public Friendship delete(Long id) {
        Friendship friendship = repository.findOne(id);
        if(friendship == null)
            throw new ServiceException("Friendship not found");
        friendship.getUser1().removeFriend(friendship.getUser2());
        friendshipNetwork.clear();
        return repository.delete(id);
    }


    public List<User> getFriendsOfUser(Long id) {
        return usersService.findOne(id).getFriends();
    }

    public List<Friendship> getFriendshipsOfUser(Long id) {
        List<Friendship> friendships = new ArrayList<>();
        for(Friendship f : repository.findAll()){
            if(f.getUser1().getId().equals(id) || f.getUser2().getId().equals(id)) friendships.add(f);
        }
        return friendships;
    }

    private void initFriendshipNetwork() {
        if(friendshipNetwork == null) {
            friendshipNetwork = new HashMap<>();
        }
        friendshipNetwork.clear();

        Iterable<User> users = usersService.findAll();
        for(User user : users){
            if(user.getFriends().isEmpty() || user.getFriends() == null) continue;
            friendshipNetwork.put(user.getId(), new HashSet<>());
        }

        Iterable<Friendship> allFriendships = repository.findAll();
        for(Friendship f : allFriendships) {
            Long u1Id = f.getUser1().getId();
            Long u2Id = f.getUser2().getId();

            if (friendshipNetwork.containsKey(u1Id) && friendshipNetwork.containsKey(u2Id)) {
                friendshipNetwork.get(u1Id).add(u2Id);
                friendshipNetwork.get(u2Id).add(u1Id);
            }
        }

    }



    public int countFriendCommunities(){
        initFriendshipNetwork();

        int components = 0;
        Set<Long> visited = new HashSet<>();
        for(Long key : friendshipNetwork.keySet()){
            if(visited.contains(key)) continue;
            components++;
            getConexComponent(visited, key);
        }
        return components;
    }

    public List<User> findMostSociableNetwork() {
        initFriendshipNetwork();

        Set<Long> visited = new HashSet<>();
        Integer bestDiameter = -1;
        Set<Long> bestComponent = Collections.emptySet();

        for(Long start : friendshipNetwork.keySet()){
            if(visited.contains(start)) continue;
            var conexComponent = getConexComponent(visited, start);
            Map.Entry<Long, Integer> farthestNodeBFS = getFarthestNodeBFS(start);
            if(farthestNodeBFS != null){
                Integer diameter = getFarthestNodeBFS(farthestNodeBFS.getKey()).getValue();

                if (diameter != null && diameter > bestDiameter) {
                    bestDiameter = diameter;
                    bestComponent = conexComponent;
                }
            }
        }


        return getUserListFromIds(bestComponent);
    }

    private List<User> getUserListFromIds(Set<Long> ids){
        List<User> users = new ArrayList<>();
        ids.forEach(id -> users.add(usersService.findOne(id)));
        return users;
    }

    private Set<Long> getConexComponent(Set<Long> visited, Long start){
        Set<Long> conexComponent =  new HashSet<>();
        Stack<Long> stack = new Stack<>();
        stack.push(start);
        visited.add(start);
        conexComponent.add(start);
        while(!stack.isEmpty()){
            Long currentNode =  stack.pop();
            for(Long neighbour : friendshipNetwork.get(currentNode)){
                if(!visited.contains(neighbour)) {
                    stack.push(neighbour);
                    visited.add(neighbour);
                    conexComponent.add(neighbour);
                }
            }
        }
        return conexComponent;
    }

    private Map.Entry<Long, Integer> getFarthestNodeBFS(Long start){
        Map<Long, Integer> dist =  new HashMap<>();
        Queue<Long> queue = new LinkedList<>();

        dist.put(start,0);
        queue.add(start);

        while(!queue.isEmpty()){
            Long currentNode =  queue.poll();
            for(Long neighbour : friendshipNetwork.get(currentNode)){
                if(!dist.containsKey(neighbour)) {
                    dist.put(neighbour,dist.get(currentNode)+1);
                    queue.add(neighbour);
                }
            }
        }

        return  dist.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }


    public List<Friendship> getGuiFriendshipsFromPage(Page<Friendship> page) {
        return StreamSupport.stream(page.getElementsOnPage().spliterator(),false).toList();
    }
}
