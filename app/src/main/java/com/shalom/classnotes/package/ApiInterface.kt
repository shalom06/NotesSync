package com.shalom.classnotes.`package`

import com.shalom.classnotes.models.Note
import io.reactivex.Observable
import retrofit2.http.GET

interface ApiInterface {

    @GET("/posts")
     fun getPosts(): Observable<List<Note>>
}