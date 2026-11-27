package br.edu.fatecfranca.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.edu.fatecfranca.api.dtos.UserRequest;
import br.edu.fatecfranca.api.entities.User;
import br.edu.fatecfranca.api.repositories.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.edu.fatecfranca.api.dtos.LoginRequest;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private void copyToEntity(
        UserRequest request,
        User user) {

        user.setFullName(request.fullname());
        user.setUsername(request.username());
        user.setEmail(request.email());
        // passwordEncoder gera o hash para a senha recebida
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setIsAdmin(request.isAdmin());
    }

    public Optional<User> authenticate(LoginRequest request) {
       String usernameOrEmail = request.usernameOrEmail().strip();

       Optional<User> result =
               repository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

       if (result.isEmpty()) {
           return Optional.empty();
       }

       User user = result.get();

       // Neste ponto ocorre a comparação entre os hashes da senha
       // digitada e da senha cadastrada
       boolean validPassword = passwordEncoder.matches(
           request.password(),
           user.getPassword()
       );

       if (!validPassword) {
           return Optional.empty();
       }

       return Optional.of(user);
   }


    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;

    }

    public User create(UserRequest request) {
        validateUniqueFields(request, null);
        User user = new User();

        copyToEntity(request, user);

        return repository.save(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    public User update(Long id, UserRequest request) {
        validateUniqueFields(request, id);
        User user = new User();

        copyToEntity(request, user);

        user.setId(id);

        return repository.save(user);
    }

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private void validateUniqueFields(UserRequest request, Long userId) {
        boolean usernameExists = userId == null
                ? repository.existsByUsername(request.username())
                : repository.existsByUsernameAndIdNot(request.username(), userId);
        if (usernameExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O nome de usuário informado já está cadastrado.");
        }

        boolean emailExists = userId == null
                ? repository.existsByEmail(request.email())
                : repository.existsByEmailAndIdNot(request.email(), userId);
        if (emailExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O e-mail informado já está cadastrado.");
        }
    }
}