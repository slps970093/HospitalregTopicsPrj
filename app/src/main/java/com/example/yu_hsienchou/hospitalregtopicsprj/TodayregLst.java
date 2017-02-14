package com.example.yu_hsienchou.hospitalregtopicsprj;

/**
 * Created by Yu-Hsien Chou on 2017/2/14.
 */

public class TodayregLst {
    public String Section;
    public String Information;
    public TodayregLst(String aSection,String aInformation){
        this.Section = aSection;
        this.Information = aInformation;
    }
    public String getSection(){
        return Section;
    }
    public String getInformation(){
        return Information;
    }
    public void setInformation(String str){
        this.Information = str;
    }
    public void setSection(String str){
        this.Section = str;
    }
}
