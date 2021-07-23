package springbootbuyitem.service;

import com.training.springbootbuyitem.BuyItemApplication;
import com.training.springbootbuyitem.entity.model.Item;
import com.training.springbootbuyitem.enums.EnumItemState;
import com.training.springbootbuyitem.error.EntityNotFoundException;
import com.training.springbootbuyitem.repository.ItemRepository;
import com.training.springbootbuyitem.service.ItemService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = BuyItemApplication.class)
@RunWith(SpringRunner.class)
public class ItemServiceTest {

	@MockBean
	private ItemRepository itemRepository;

	@Autowired
	private ItemService itemService;

	private static final String ITEM_NAME = "banana";
	private static final long ID = 1l;

	@Test
 	public void save() {
		Item item = Item.builder().
				name(ITEM_NAME).priceTag(BigDecimal.ONE).stock(BigInteger.ONE).build();

				Item persist = item;
				persist.setItemUid(1l);
		when(itemRepository.save(item)).thenReturn(persist);
		item = itemService.save(item);
		assertThat(item.getItemUid(),is(1l));
	}

	@Test
	public void Get() {
		Item item = Item.builder().
				name(ITEM_NAME).priceTag(BigDecimal.ONE).stock(BigInteger.ONE).build();
		Item persist = item;
		persist.setItemUid(ID);
		when(itemRepository.findById(ID)).thenReturn(Optional.of(persist));

		item = itemService.get(ID);

		assertThat("Should match item id", item, hasProperty("itemUid", is(ID)));
		assertThat("Should match item name", item, hasProperty("name", is(ITEM_NAME)));
		assertThat("Should match item stock", item, hasProperty("priceTag", is(BigDecimal.ONE)));
	}

	@Test(expected = EntityNotFoundException.class)
	public void getNotFound() {
		when(itemRepository.findById(ID)).thenReturn(Optional.empty());
		itemService.get(ID);
	}

	@Test
	public void getMultiple() {
		List<Long> idList = Arrays.asList(1l, 2l, 4l);
		Item item1 = Item.builder().name("item1").itemUid(1l).build();
		Item item2 = Item.builder().name("item2").itemUid(2l).build();
		Item item4 = Item.builder().name("item4").itemUid(4l).build();
		List<Item> itemList = Arrays.asList(item1, item2, item4);

		when(itemRepository.findAllById(idList)).thenReturn(itemList);

		assertThat("Should return valid item list", itemService.get(idList), Matchers.containsInAnyOrder(itemList.toArray()));
	}

	@Test
	public void list() {
		Item item1 = Item.builder().name("item1").itemUid(1l).build();
		Item item2 = Item.builder().name("item2").itemUid(2l).build();
		Item item3 = Item.builder().name("item3").itemUid(3l).build();
		Item item4 = Item.builder().name("item4").itemUid(4l).build();
		List<Item> itemList = Arrays.asList(item1, item2, item3, item4);

		when(itemRepository.findAll()).thenReturn(itemList);

		assertThat("Should return valid item list", itemService.list(), Matchers.containsInAnyOrder(itemList.toArray()));
	}

	@Test
	public void update() {
		Long id = 1l;
		String itemName = "item1";
		String updatedName = "item updated";
		String description = "desc ";
		String updatedDesc = "desc updated";
		String market = "PT";
		String updatedMarket = "ES";

		Item item1 = Item.builder().name(itemName).itemUid(id).priceTag(BigDecimal.TEN).stock(BigInteger.TEN)
				.description(description).market(market).state(EnumItemState.AVAILABLE.name())
				.build();

		Item updatedItem = Item.builder().name(updatedName).itemUid(id).priceTag(BigDecimal.ZERO).stock(BigInteger.ZERO)
				.description(updatedDesc).market(updatedMarket).state(EnumItemState.RESTOCK.name())
				.build();

		when(itemRepository.findById(id)).thenReturn(Optional.of(item1));
		when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

		assertThat("Should have updated id", itemService.update(updatedItem), hasProperty("itemUid", is(id)));
		assertThat("Should have updated name", itemService.update(updatedItem), hasProperty("name", is(updatedName)));
		assertThat("Should have updated priceTag", itemService.update(updatedItem), hasProperty("priceTag", is(BigDecimal.ZERO)));
		assertThat("Should have updated stock", itemService.update(updatedItem), hasProperty("stock", is(BigInteger.ZERO)));
		assertThat("Should have updated description", itemService.update(updatedItem), hasProperty("description", is(updatedDesc)));
		assertThat("Should have updated market", itemService.update(updatedItem), hasProperty("market", is(updatedMarket)));
		assertThat("Should have updated state", itemService.update(updatedItem), hasProperty("state", is(EnumItemState.RESTOCK.name())));
	}

	@Test
	public void updateNegativePrice() {
		Long id = 1l;
		String itemName = "item1";
		String description = "desc";
		String market = "PT";
		Item item1 = Item.builder().name(itemName)
				.itemUid(id)
				.priceTag(BigDecimal.TEN)
				.stock(BigInteger.TEN)
				.description(description).market(market).state(EnumItemState.AVAILABLE.name())
				.build();

		Item updatedItem = Item.builder().name(itemName)
				.itemUid(id)
				.priceTag(BigDecimal.ONE.negate())
				.stock(BigInteger.TEN)
				.description(description).market(market).state(EnumItemState.AVAILABLE.name())
				.build();

		when(itemRepository.findById(id)).thenReturn(Optional.of(item1));
		when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

		assertThat("Should have updated id", itemService.update(updatedItem), hasProperty("itemUid", is(id)));
		assertThat("Should have updated name", itemService.update(updatedItem), hasProperty("name", is(itemName)));
		assertThat("Should not have updated priceTag", itemService.update(updatedItem), hasProperty("priceTag", is(BigDecimal.TEN)));
	}
	
	// TODO test for every other field invalid update case


}