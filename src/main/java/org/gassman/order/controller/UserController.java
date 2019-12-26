package org.gassman.order.controller;

import org.gassman.order.entity.User;
import org.gassman.order.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageChannel userRegistrationChannel;

    @GetMapping
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<>(userRepository.findByActiveTrue(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findUserById(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }

    }

    @PostMapping
    public ResponseEntity<User> postUser(@RequestBody User user){
        User userInDB = userRepository.findByMail(user.getMail());
        if(userInDB != null) {
            userInDB.setActive(Boolean.TRUE);
            userRepository.save(userInDB);
            sendUserRegistrationMessage(userInDB);
            return new ResponseEntity<>(userInDB, HttpStatus.CREATED);
        } else {
            User userPersisted = userRepository.save(user);
            sendUserRegistrationMessage(user);
            return new ResponseEntity<>(userPersisted, HttpStatus.CREATED);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<User> putUser(@PathVariable Long id, @RequestBody User user){
        if(userRepository.existsById(id)){
            user.setId(id);
            return new ResponseEntity<>(userRepository.save(user), HttpStatus.ACCEPTED);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            user.get().setActive(Boolean.FALSE);
            userRepository.save(user.get());
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    @DeleteMapping("/telegram/{id}")
    public ResponseEntity<Boolean> deleteUserByTelegram(@PathVariable Integer id){
        Optional<User> user = userRepository.findByTelegramUserId(id);
        if(user.isPresent()){
            user.get().setActive(Boolean.FALSE);
            userRepository.save(user.get());
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("ID %d does not exists",id), null);
        }
    }

    private void sendUserRegistrationMessage(@RequestBody User user) {
        Message<User> msg = MessageBuilder.withPayload(user).build();
        userRegistrationChannel.send(msg);
    }
}
