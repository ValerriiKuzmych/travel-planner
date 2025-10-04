package io.github.valeriikuzmych.travelplanner.service;

public interface IUserService {

  void  registerUser(String email, String password);
  boolean  authenticate (String email, String password);
}
