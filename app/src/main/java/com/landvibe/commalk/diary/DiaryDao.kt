package com.landvibe.commalk.diary

import androidx.room.*
import com.landvibe.commalk.diary.Diary

@Dao
/*
    Dao = Data Access Object
    DB에 있는 접근을 위한 인터페이스
    memo 테이블에 id, title, content, alarm이 있다고 했는데,
    (1, "제목1", "메모1", 111111)
    (2, "제목2", "메모2", 111111)
    (3, "제목3", "메모3", 111111)
    (4, "제목4", "메모4", 111111) 이렇게 데이터가 있다고 치면
    각각의 주소와 같은 정보를 얻어 올 수 있다
 */
interface DiaryDao {
    /*
        (1, "제목1", "메모1", 111111)
        (2, "제목2", "메모2", 111111)
        (3, "제목3", "메모3", 111111)
        (4, "제목4", "메모4", 111111)
        의 리스트가 결과
     */
    @Query("SELECT * FROM diary")
    fun getAll(): List<Diary>

    /*
        파라미터로 1을 입력하면
        (1, "제목1", "메모1", 111111)
        하나의 객체가 결과
     */

    @Query("SELECT * FROM diary WHERE id = :id")
    fun get(id: Int): Diary

    /*
        Memo 모델을 파라미터로 호출하면 추가 된다
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diary: Diary)

    /*
        Memo 모델을 파라미터로 호출하면 삭제 된다.
        삭제하는 값을 비교하는 기준은 PrimaryKey이다.
     */
    @Delete
    fun delete(diary: Diary)

}