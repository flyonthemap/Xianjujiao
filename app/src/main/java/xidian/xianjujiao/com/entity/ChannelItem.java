package xidian.xianjujiao.com.entity;

import java.io.Serializable;

/**
 * ITEM的对应可序化队列属性
 *  */
public class ChannelItem implements Serializable {

	private static final long serialVersionUID = -6465237897027410019L;
	// 栏目对应的Id
	public String id;
	// 栏目对应的名字
	public String name;
	// 栏目的排序顺序
	public Integer orderId;

	// 栏目是否被选中
	public Integer selected;

	public ChannelItem() {
	}

	public ChannelItem(String id, String name, int orderId, int selected) {
		this.id = id;
		this.name = name;
		this.orderId = Integer.valueOf(orderId);
		this.selected = Integer.valueOf(selected);
	}

    public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getOrderId() {
		return this.orderId.intValue();
	}

	public Integer getSelected() {
		return this.selected;
	}

	public void setId(String paramInt) {
		this.id = paramInt;
	}

	public void setName(String paramString) {
		this.name = paramString;
	}

	public void setOrderId(int paramInt) {
		this.orderId = Integer.valueOf(paramInt);
	}

	public void setSelected(Integer paramInteger) {
		this.selected = paramInteger;
	}

    @Override
    public String toString() {
        return "ChannelItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", orderId=" + orderId +
                ", selected=" + selected +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChannelItem that = (ChannelItem) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (orderId != null ? !orderId.equals(that.orderId) : that.orderId != null) return false;
        return selected != null ? selected.equals(that.selected) : that.selected == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
        result = 31 * result + (selected != null ? selected.hashCode() : 0);
        return result;
    }
}