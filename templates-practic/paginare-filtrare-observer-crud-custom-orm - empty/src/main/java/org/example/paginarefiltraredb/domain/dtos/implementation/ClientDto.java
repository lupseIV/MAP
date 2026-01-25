//package org.example.paginarefiltraredb.domain.dtos.implementation;
//
//public class ClientDto {
//    private final Integer id;
//    private final String name;
//    private final String budget; // String instead of Double for "$500.00"
//    private final String type;
//
//    public ClientDto(Client client) {
//        this.id = client.getId();
//        this.name = client.getName();
//        this.type = client.getType();
//        // Format money nicely for the GUI
//        this.budget = String.format("$%.2f", client.getBudget());
//    }
//
//    public ClientDto(Integer id, String name, String budget, String type) {
//        this.id = id;
//        this.name = name;
//        this.budget = budget;
//        this.type = type;
//    }
//
//    // Getters are required for PropertyValueFactory
//    public Integer getId() { return id; }
//    public String getName() { return name; }
//    public String getBudget() { return budget; }
//    public String getType() { return type; }
//}