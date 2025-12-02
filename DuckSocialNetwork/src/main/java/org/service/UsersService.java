package org.service;

import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.dtos.guiDTOS.UserGuiDTO;
import org.domain.users.duck.Duck;
import org.domain.users.relationships.Friendship;
import org.domain.users.person.Person;
import org.domain.users.User;
import org.domain.exceptions.ServiceException;
import org.repository.util.paging.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class UsersService implements Service<Long, User> {
    private final DucksService ducksService;
    private final PersonsService personsService;
    private final FriendshipService friendshipService;

    public UsersService(DucksService ducksService, PersonsService personsService, FriendshipService friendshipService) {
        this.ducksService = ducksService;
        this.personsService = personsService;
        this.friendshipService = friendshipService;
    }

    public User findOne(Long id) {
        User user = ducksService.findOne(id);
        if(user == null){
            user = personsService.findOne(id);
        }
        return user;
    }

    public Iterable<User> findAll() {
        return Stream.concat(StreamSupport.stream(ducksService.findAll().spliterator(),false),
                            StreamSupport.stream(personsService.findAll().spliterator(),false)).toList();
    }

    public User save(User entity) {
        if(entity instanceof Duck) return ducksService.save((Duck) entity);
        if(entity instanceof Person) return  personsService.save((Person) entity);
        throw new ServiceException("Invalid user type");
    }

    public User delete(Long id) {
        User user = findOne(id);
        if (user == null) throw new ServiceException("No user found with id " + id);

        var friendships = friendshipService.getFriendshipsOfUser(id);
        for (Friendship friendship : friendships) {
            friendshipService.delete(friendship.getId());
        }

        if (user instanceof Duck) return ducksService.delete(id);
        else return personsService.delete(id);
    }

    public User update(User entity) {
        User user = ducksService.findOne(entity.getId());
        if(user != null) {
            return ducksService.update((Duck) entity);
        } else {
            user = personsService.findOne(entity.getId());
            if(user != null) {
                return personsService.update((Person) entity);
            } else {
                throw new ServiceException("No user found with id " + entity.getId());
            }
        }
    }

    public List<UserGuiDTO> getGuiUsers(){
        List<UserGuiDTO> list = new ArrayList<>();
        for(User u : findAll()){
            String username = u.getUsername();
            String email = u.getEmail();
            int nrOfFriends = u.getFriends().size();


            list.add(new UserGuiDTO(username,email,nrOfFriends));
        }
        return list;
    }

    public List<UserGuiDTO> getGuiUsersFromPage(Page<User> page){
        List<UserGuiDTO> list = new ArrayList<>();

        page.getElementsOnPage().forEach(user -> {
            String username = user.getUsername();
            String email = user.getEmail();
            int nrOfFriends = user.getFriends().size();

            list.add(new UserGuiDTO(username,email,nrOfFriends));
        });
        return list;
    }
}
