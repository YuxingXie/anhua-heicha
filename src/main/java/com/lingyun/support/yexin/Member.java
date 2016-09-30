package com.lingyun.support.yexin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/29.
 */
public class Member {
    public Member(int index){
        this.index=index;
        int totalPerson=1;
        int levelTotalPerson=1;
        if (index==1) this.level=1;
        else{
            for(int l=1;l<=index;l++){
                if (l>1) {
                    levelTotalPerson*=2;
                    totalPerson+=levelTotalPerson;
                    if (index>(totalPerson-levelTotalPerson)&&index<=totalPerson){
                        this.level=l;
                        break;
                    }
                }

            }
        }

    }
    public List<Member> getAllMemberBeforeIndex(){
        List<Member> members=new ArrayList<Member>();
        if (index==1) {
            members.add(this);
            return members;
        }
        for (int i=1;i<=index;i++){
            members.add(new Member(i));
        }
        return members;
    }
    public int howManyPersonsGetRankBonusInLevel(){
        List<Member> members=getAllMemberBeforeIndex();
        for (Member member:members){

        }
        return 0;
    }
    public int level;
    public int index;
    public int levelIndex;
    public boolean rank1;
    public boolean rank2;
    public boolean rank3;
    public double achieve;
    public boolean equals(Member another){
        return index==another.index;
    }

    public static void main(String[] args){
        Member member5=new Member(1);
        Member member6=new Member(2);
        Member member1=new Member(4);
        Member member2=new Member(8);
        Member member3=new Member(15);
        Member member4=new Member(17);
        System.out.println(member5.level);
        System.out.println(member6.level);
        System.out.println(member1.level);
        System.out.println(member2.level);
        System.out.println(member3.level);
        System.out.println(member4.level);
    }
}
