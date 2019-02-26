package marco.dao;

public interface DaoEvents {
	String TOPIC = "dev/persistence/dao/";
	
	String PRODUCT_TOPIC = TOPIC + "product/";
	String PRODUCT_ALL =  PRODUCT_TOPIC + "*";
	String PRODUCT_PRE_PERSIST = PRODUCT_TOPIC + "pre_persist";
	String PRODUCT_POST_PERSIST = PRODUCT_TOPIC + "post_persist";
	String PRODUCT_PRE_UPDATE = PRODUCT_TOPIC + "pre_update";
	String PRODUCT_POST_UPDATE = PRODUCT_TOPIC + "post_update";
	String PRODUCT_PRE_REMOVE = PRODUCT_TOPIC + "pre_remove";
	String PRODUCT_POST_REMOVE = PRODUCT_TOPIC + "post_remove";
	String PRODUCT_ID = "product_id";
	String PRODUCT_SLUG = "product_slug";

	String CATEGORY_TOPIC = TOPIC + "category/";
	String CATEGORY_ALL =  CATEGORY_TOPIC + "*";
	String CATEGORY_PRE_PERSIST = CATEGORY_TOPIC + "pre_persist";
	String CATEGORY_POST_PERSIST = CATEGORY_TOPIC + "post_persist";
	String CATEGORY_PRE_UPDATE = CATEGORY_TOPIC + "pre_update";
	String CATEGORY_POST_UPDATE = CATEGORY_TOPIC + "post_update";
	String CATEGORY_PRE_REMOVE = CATEGORY_TOPIC + "pre_remove";
	String CATEGORY_POST_REMOVE = CATEGORY_TOPIC + "post_remove";
	String CATEGORY_ID = "category_id";
	String CATEGORY_SLUG = "category_slug";

	String CART_TOPIC = TOPIC + "cart/";
	String CART_ALL =  CART_TOPIC + "*";
	String CART_PRE_PERSIST = CART_TOPIC + "pre_persist";
	String CART_POST_PERSIST = CART_TOPIC + "post_persist";
	String CART_PRE_UPDATE = CART_TOPIC + "pre_update";
	String CART_POST_UPDATE = CART_TOPIC + "post_update";
	String CART_PRE_REMOVE = CART_TOPIC + "pre_remove";
	String CART_POST_REMOVE = CART_TOPIC + "post_remove";
	String CART_ID = "cart_id";

	String CART_SESSION = "cart_session";

	String POSITION_TOPIC = TOPIC + "positions/";
	String POSITION_ALL =  POSITION_TOPIC + "*";
	String POSITION_PRE_PERSIST = POSITION_TOPIC + "pre_persist";
	String POSITION_POST_PERSIST = POSITION_TOPIC + "post_persist";
	String POSITION_PRE_UPDATE = POSITION_TOPIC + "pre_update";
	String POSITION_POST_UPDATE = POSITION_TOPIC + "post_update";
	String POSITION_PRE_REMOVE = POSITION_TOPIC + "pre_remove";
	String POSITION_POST_REMOVE = POSITION_TOPIC + "post_remove";
	String POSITION_ID = "positions_id";

	String PROPERTY_TOPIC = TOPIC + "property/";
	String PROPERTY_ALL = PROPERTY_TOPIC + "*";
	String PROPERTY_PRE_PERSIST = PROPERTY_TOPIC + "pre_persist";
	String PROPERTY_POST_PERSIST = PROPERTY_TOPIC + "post_persist";
	String PROPERTY_PRE_UPDATE = PROPERTY_TOPIC + "pre_update";
	String PROPERTY_POST_UPDATE = PROPERTY_TOPIC + "post_update";
	String PROPERTY_PRE_REMOVE = PROPERTY_TOPIC + "pre_remove";
	String PROPERTY_POST_REMOVE = PROPERTY_TOPIC + "post_remove";
	String PROPERTY_ID = "property_id";
	String PROPERTY_SLUG = "property_slug";
	String PROPERTY_DENOMINATOR = "property_denominator";

	String STOCKRECORD_TOPIC = TOPIC + "stockrecord/";
	String STOCKRECORD_ALL = STOCKRECORD_TOPIC + "*";
	String STOCKRECORD_PRE_PERSIST = STOCKRECORD_TOPIC + "pre_persist";
	String STOCKRECORD_POST_PERSIST = STOCKRECORD_TOPIC + "post_persist";
	String STOCKRECORD_PRE_UPDATE = STOCKRECORD_TOPIC + "pre_update";
	String STOCKRECORD_POST_UPDATE = STOCKRECORD_TOPIC + "post_update";
	String STOCKRECORD_PRE_REMOVE = STOCKRECORD_TOPIC + "pre_remove";
	String STOCKRECORD_POST_REMOVE = STOCKRECORD_TOPIC + "post_remove";
	String STOCKRECORD_ID = "stockrecord_id";
	String STOCKRECORD_SLUG = "stockrecord_slug";

	String ERROR = "error";
}
