package cn.retech.domainbean_model.booklist_in_bookstores;

import java.io.Serializable;

public final class BookInfo implements Cloneable, Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = -5224150072418061091L;

  // 一本书籍的唯一性 标识ID
  private String content_id;
  // 书籍名称
  private String name;
  // 书籍发行日期
  private String published;
  // 书籍过期时间
  private String expired;
  // 作者
  private String author;
  // 价钱
  private String price;
  // 书籍对应的产品ID, 用于收费书籍的购买行为
  private String productid;
  // 书籍归属的类别ID
  private String categoryid;
  // 出版社/发行人
  private String publisher;
  // 书籍封面图片URL地址
  private String thumbnail;
  // 书籍描述
  private String description;
  // 书籍zip资源包大小, 以byte为单位.
  private String size;

  private BookInfo() {

  }

  public BookInfo(String content_id, String name, String published, String expired, String author, String price, String productid, String categoryid, String publisher, String thumbnail,
      String description, String size) {
    this.content_id = content_id;
    this.name = name;
    this.published = published;
    this.expired = expired;
    this.author = author;
    this.price = price;
    this.productid = productid;
    this.categoryid = categoryid;
    this.publisher = publisher;
    this.thumbnail = thumbnail;
    this.description = description;
    this.size = size;
  }

  public String getContent_id() {
    return content_id;
  }

  public String getName() {
    return name;
  }

  public String getPublished() {
    return published;
  }

  public String getExpired() {
    return expired;
  }

  public String getAuthor() {
    return author;
  }

  public String getPrice() {
    return price;
  }

  public String getProductid() {
    return productid;
  }

  public String getCategoryid() {
    return categoryid;
  }

  public String getPublisher() {
    return publisher;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public String getDescription() {
    return description;
  }

  public String getSize() {
    return size;
  }

  @Override
  public String toString() {
    return "BookInfo [content_id=" + content_id + ", name=" + name + ", published=" + published + ", expired=" + expired + ", author=" + author + ", price=" + price + ", productid=" + productid
        + ", categoryid=" + categoryid + ", publisher=" + publisher + ", thumbnail=" + thumbnail + ", description=" + description + ", size=" + size + "]";
  }

  @Override
  public BookInfo clone() {
    BookInfo cloneObject = new BookInfo();
    cloneObject.content_id = this.content_id;
    cloneObject.name = this.name;
    cloneObject.published = this.published;
    cloneObject.expired = this.expired;
    cloneObject.author = this.author;
    cloneObject.price = this.price;
    cloneObject.productid = this.productid;
    cloneObject.categoryid = this.categoryid;
    cloneObject.publisher = this.publisher;
    cloneObject.thumbnail = this.thumbnail;
    cloneObject.description = this.description;
    cloneObject.size = this.size;
    return cloneObject;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((content_id == null) ? 0 : content_id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BookInfo other = (BookInfo) obj;
    if (content_id == null) {
      if (other.content_id != null)
        return false;
    } else if (!content_id.equals(other.content_id))
      return false;
    return true;
  }

}
