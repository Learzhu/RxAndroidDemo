package com.learzhu.rxandroiddemo.data;

/**
 * Data.java是RxAndroidDemo的类。
 *
 * @author Learzhu
 * @version 2.0.0 2019-04-11 16:29
 * @update Learzhu 2019-04-11 16:29
 * @updateDes
 * @include {@link }
 * @used {@link }
 */
public class Data {
    private int id;
    private String name;

    public Data(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
