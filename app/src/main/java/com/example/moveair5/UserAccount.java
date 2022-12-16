package com.example.moveair5;

//사용자 계정 정보 모델 클래스
public class UserAccount {
    private String idToken; //firebase uid(고유 토큰정보)
    private String emailId; // 이메일 아이디

    public UserAccount() {
    }//파이어베이스에서는 빈 생성자 안 만들면 오류 발생

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
