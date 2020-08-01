package com.example.kjc;

public class Users
{
    public String fullname, profileimage,course,thumbs,body,heading;
    public Users()
    {

    }


    public Users(String fullname, String profileimage, String course,String thumbs,String body,String heading) {
        this.fullname = fullname;
        this.profileimage = profileimage;
        this.course = course;
        this.thumbs = thumbs;
        this.body = body;
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getFullname() {
        return fullname;
    }

    public String getThumbs() {
        return thumbs;
    }

    public void setThumbs(String thumbs) {
        this.thumbs = thumbs;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

}
