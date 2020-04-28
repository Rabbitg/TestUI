package com.hour.uigithub.database

class Note {
    var id: String?= null
    var title: String?= null
    var content: String?= null

    constructor(){}

    constructor(id:String, title:String, content:String){
        this.id=id
        this.title=title
        this.content=content
    }

    constructor(title:String,content: String){
        this.title=title
        this.content= content
    }

    fun toMap():Map<String, Any>{
        val result = HashMap<String,Any>()
        result["title"] = title!!
        result["content"] = content!!
        return result
    }
}