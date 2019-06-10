package com.gy.fof.step2;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class FoFEntity implements WritableComparable<FoFEntity> {

    private String uname;
    private int score;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoFEntity foFEntity = (FoFEntity) o;
        return score == foFEntity.score &&
                Objects.equals(uname, foFEntity.uname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uname, score);
    }

    @Override
    public int compareTo(FoFEntity _this) {
        boolean f1 = this.getUname().equals(_this.getUname());
        if(f1){
            return this.getScore() < _this.getScore()?-1:this.getScore() == _this.getScore()?0:1;
        }
        return 1;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.getUname());
        out.writeInt(this.getScore());
    }

    @Override
    public void readFields(DataInput in) throws IOException {
            this.setUname(in.readUTF());
            this.setScore(in.readInt());
    }
}
