package com.example.library

import android.widget.Filter

class FilterPdf: Filter {

    var filterList: ArrayList<ModelPdf>

    var adapterPdf: AdapterPdf

    constructor(filterList: ArrayList<ModelPdf>, adapterPdf: AdapterPdf) {
        this.filterList = filterList
        this.adapterPdf = adapterPdf
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices){
                if (filterList[i].title.lowercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

    }


}