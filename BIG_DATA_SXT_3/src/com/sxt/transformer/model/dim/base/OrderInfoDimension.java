package com.sxt.transformer.model.dim.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class OrderInfoDimension extends BaseDimension{

    private int id; // id
    private String paymentType; // 名称

    public OrderInfoDimension() {
        super();
    }

    public OrderInfoDimension(String paymentType) {
        super();
        this.paymentType = paymentType;
    }

    public void clean() {
        this.id = 0;
        this.paymentType = "";
    }

    public static OrderInfoDimension newInstance(String paymentType) {
        return new OrderInfoDimension(paymentType);
    }

    public static OrderInfoDimension buildDimension(String paymentType) {
        return new OrderInfoDimension(paymentType);
    }


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.paymentType);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.paymentType = in.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this == o) {
            return 0;
        }
        OrderInfoDimension other = (OrderInfoDimension) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.paymentType.compareTo(other.paymentType);
        if (tmp != 0) {
            return tmp;
        }
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderInfoDimension that = (OrderInfoDimension) o;
        return id == that.id &&
                Objects.equals(paymentType, that.paymentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
