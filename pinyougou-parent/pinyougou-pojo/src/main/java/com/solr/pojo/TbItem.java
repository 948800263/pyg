package com.solr.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class TbItem implements Serializable{
	@Field
	 private Long id;
	@Field("item_title")
    private String title;
	    
    @Field("item_price")
	private BigDecimal price;
	
    @Field("item_image")
    private String image;

    @Field("item_goodsid")
    private Long goodsId;

    @Field("item_category")
    private String category;

    @Field("item_brand")
    private String brand;

    @Field("item_seller")
    private String seller;
	
	

	

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title == null ? null : title.trim();
	    }

	 

	    public BigDecimal getPrice() {
	        return price;
	    }

	    public void setPrice(BigDecimal price) {
	        this.price = price;
	    }

	  
	    public String getImage() {
	        return image;
	    }

	    public void setImage(String image) {
	        this.image = image == null ? null : image.trim();
	    }

	   
	    public String getCategory() {
	        return category;
	    }

	    public void setCategory(String category) {
	        this.category = category == null ? null : category.trim();
	    }

	    public String getBrand() {
	        return brand;
	    }

	    public void setBrand(String brand) {
	        this.brand = brand == null ? null : brand.trim();
	    }


	    public String getSeller() {
	        return seller;
	    }

	    public void setSeller(String seller) {
	        this.seller = seller == null ? null : seller.trim();
	    }

		@Override
		public String toString() {
			return "TbItem [id=" + id + ", title=" + title + ", price=" + price + ", image=" + image + ", goodsId="
					+ goodsId + ", category=" + category + ", brand=" + brand + ", seller=" + seller + "]";
		}
	    
	    
}
