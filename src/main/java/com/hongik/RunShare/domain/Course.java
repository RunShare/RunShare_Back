/*
  도메인 객체 Course.java
* */

package com.hongik.RunShare.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "file", insertable = false, updatable = false)
    private Long file;

    @Builder
    public Course(String name, String content){
        this.name = name;
        this.content = content;
    }

    public void update(String name, String content){
        this.name = name;
        this.content = content;
    }
}

