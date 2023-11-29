package com.example.nasapictureoftheday

class APIFormat (
    var results:List<Result>
)

class Result(
    var copyright:String,
    var date:String,
    var title:String,
    var url:String
)