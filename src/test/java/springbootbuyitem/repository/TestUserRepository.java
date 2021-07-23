package springbootbuyitem.repository;

import com.training.springbootbuyitem.BuyItemApplication;
import com.training.springbootbuyitem.entity.model.Item;
import com.training.springbootbuyitem.entity.model.User;
import com.training.springbootbuyitem.enums.EnumEntity;
import com.training.springbootbuyitem.error.EntityNotFoundException;
import com.training.springbootbuyitem.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@SpringBootTest(classes = BuyItemApplication.class)
@RunWith(SpringRunner.class)
public class TestUserRepository {

    @Autowired
    private UserRepository userRepository;

    @Sql("/delete_all.sql")
    @Test
    public void createUserTest() {
        User user = User.builder()
                .name("userTest")
                .username("usernameTest")
                .password("passwordTest")
                .build();

        User userSaved = userRepository.save(user);
        assertThat("Should have same name", userSaved, hasProperty("name", is("userTest")));
    }

    @Sql("/delete_all.sql")
    @Test
    public void updateUserTest() {
        String newName = "new name";
        User user = User.builder()
                .name("userTest")
                .username("usernameTest")
                .password("passwordTest")
                .build();
        userRepository.save(user);

        user.setName(newName);
        User updated = userRepository.save(user);

        assertThat("Should update name", updated, hasProperty("name", is(newName)));
    }

    @Test
    @SqlGroup({
            @Sql("/delete_all.sql"),
            @Sql("/insertUsers.sql")
    })
    public void geUsersTest() {
        assertThat(userRepository.findAll().size(), is(5));
    }

    @Sql("/delete_all.sql")
    @Test(expected = EntityNotFoundException.class)
    public void deleteUserTest() {
        User user = User.builder()
                .name("userTest")
                .username("usernameTest")
                .password("passwordTest")
                .build();
        User userSaved = userRepository.save(user);

        userRepository.delete(userSaved);

        userRepository.findById(user.getUserUid())
                .orElseThrow(() -> new EntityNotFoundException(EnumEntity.USER.name(), userSaved.getUserUid()));
    }

    @Sql("/delete_all.sql")
    @Test(expected = DataIntegrityViolationException.class)
    public void createDuplicateItemTest() {
        userRepository.save(User.builder()
                .name("userTest")
                .username("usernameTest")
                .password("passwordTest")
                .build());
        userRepository.save(User.builder()
                .name("userTest")
                .username("usernameTest")
                .password("passwordTest")
                .build());
    }
}
