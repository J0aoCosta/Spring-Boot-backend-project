package springbootbuyitem;

import com.training.springbootbuyitem.BuyItemApplication;
import com.training.springbootbuyitem.controller.BuyController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BuyItemApplication.class)
class ItemStorageApplicationTests {
	@Autowired
	private BuyController buyController;

	@Test
	void contextLoads() {
		assertThat(buyController).isNotNull();
	}

}
