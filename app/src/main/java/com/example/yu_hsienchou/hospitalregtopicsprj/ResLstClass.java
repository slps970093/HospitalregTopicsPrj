package com.example.yu_hsienchou.hospitalregtopicsprj;

/**
 * Created by Yu-Hsien Chou on 2017/2/16.
 */

public class ResLstClass  {
    private String aName;
    private String aInfo;
    public ResLstClass(String aName,String aInfo){
        this.aName = aName;
        this.aInfo = aInfo;
    }
    public void setaName(String str){
        this.aName = str;
    }
    public void setaInfo(String str){
        this.aInfo = str;
    }
    public String getaName(){
        return  this.aName;
    }
    public String getaInfo(){
        return  this.aInfo;
    }
}
