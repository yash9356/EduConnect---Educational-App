package com.example.alphahour.utils

interface ItemListener<in ITEM : Any> : ItemClickedListener<ITEM> {
    var selectedPosition: Int
}

interface ItemClickedListener<in ITEM : Any> {
    fun onItemClicked(item: ITEM, position: Int)
}