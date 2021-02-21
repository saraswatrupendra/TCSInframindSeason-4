package com.example.tcs_health_system.Modal;

public class ChatMesssage {
    String Smsg,Rmsg;

    public ChatMesssage(String smsg, String rmsg) {
        Smsg = smsg;
        Rmsg = rmsg;
    }

    public String getSmsg() {
        return Smsg;
    }

    public void setSmsg(String smsg) {
        Smsg = smsg;
    }

    public String getRmsg() {
        return Rmsg;
    }

    public void setRmsg(String rmsg) {
        Rmsg = rmsg;
    }
}
