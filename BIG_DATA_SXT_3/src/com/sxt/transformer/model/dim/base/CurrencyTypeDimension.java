package com.sxt.transformer.model.dim.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author root
 */
public class CurrencyTypeDimension extends BaseDimension {
    private int id; // id
    private String currencyName; // 名称

    public CurrencyTypeDimension() {
        super();
    }

    public CurrencyTypeDimension(String currencyName) {
        super();
        this.currencyName = currencyName;
    }

    public void clean() {
        this.id = 0;
        this.currencyName = "";
    }

    public static CurrencyTypeDimension newInstance(String currencyName) {
        return new CurrencyTypeDimension(currencyName);
    }

    public static CurrencyTypeDimension buildDimension(String currencyName) {
        return new CurrencyTypeDimension(currencyName);
    }


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.id);
        out.writeUTF(this.currencyName);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.id = in.readInt();
        this.currencyName = in.readUTF();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this == o) {
            return 0;
        }

        CurrencyTypeDimension other = (CurrencyTypeDimension) o;
        int tmp = Integer.compare(this.id, other.id);
        if (tmp != 0) {
            return tmp;
        }
        tmp = this.currencyName.compareTo(other.currencyName);
        if (tmp != 0) {
            return tmp;
        }
        return tmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyTypeDimension that = (CurrencyTypeDimension) o;
        return id == that.id &&
                Objects.equals(currencyName, that.currencyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}
