package com.study.board.post;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@Entity
@Table(name="post")
public class Post {
    // UUID(8-4-4-4-12자리 16진수) + "." + jpg, jpeg, png
    // 이미지 이름을 정규표현식으로 검사(post 보낼때 아무 글자를 넣어도 통과되는 문제 때문에)
    private static final Pattern IMAGE_NAME = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|jpeg|png)$");
    @Id
    // GenratedValue : 자동으로 만들어지는 어노테이션
    // GenrationType.IDENTITY : DB의 Auto_increment
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "member_id")
    private Long memberId;    // 자바는 camelCase. DB 열 이름은 member_id (PostMapper.xml 의 column 은 DB 이름)

    @Column(name = "image_name")
    private String imageName;

    protected Post() {}

    // 제목/내용 규칙. 만들 때(생성자)와 수정할 때(update) 둘 다 쓰므로 memberId 는 여기에 넣지 않았다
    private void validate(String title, String content){
        if(title == null || title.isBlank() || content == null || content.isBlank()){
            throw new IllegalArgumentException("제목 혹은 내용이 비었습니다.");
        }
        // title varchar255 이기 때문에 예외 던져야함, 그리고 길이 예외는 null 검사 이후에 하기
        if(title.length() > 255){
            throw new IllegalArgumentException("제목의 길이는 255자 이하여야 합니다.");
        }
    }
    // 이미지 검사
    private void validateImageName(String imageName){
        // 이미지가 없는 글은 통과
        if(imageName == null){
            return;
        }
        // 이미지 이름의 형식이 우리가 정한것과 다르면 예외
        if(!IMAGE_NAME.matcher(imageName).matches()){
            throw new IllegalArgumentException("이미지 이름이 올바르지 않습니다.");
        }
    }

    // 파라미터 순서는 PostMapper.xml 의 <constructor> 안 <idArg>/<arg> 순서와 같아야 한다.
    // DB 에서 읽은 행도 이 생성자를 거치므로 규칙을 통과해야 한다.
    public Post(Long id, String title, String content, Long memberId, String imageName) {
        validate(title, content);
        validateImageName(imageName);
        // 글쓴이는 "만들 때만" 필요하고 수정되지 않는 값이라 생성자에서만 검사한다 (없으면 400)
        if(memberId == null){
            throw new IllegalArgumentException("작성자가 없습니다.");
        }
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberId = memberId;
        this.imageName = imageName;
    }

    public void update(String title, String content){
        validate(title, content);
        this.title = title;
        this.content = content;
    }

}
