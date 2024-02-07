package kr.co.devicechecker.base.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kr.co.devicechecker.base.bind.DataBindingConfig
import timber.log.Timber

abstract class BaseDataBindingAdapter<M, B : ViewDataBinding>(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // recycler view item lists
    protected val itemList: MutableList<M> = mutableListOf()

    // getter Item
    fun getItem(position: Int): M {
        return itemList[position]
    }

    // getter Item Count
    override fun getItemCount(): Int {
        return itemList.size
    }

    // set Item List
    @SuppressLint("NotifyDataSetChanged")
    fun setItemList(list: List<M>?) {
        Timber.d("onSetItemList... %s", list?.isEmpty())
        requireNotNull(list)
        // Timber.d("setItemList list = $list")
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    // 리사이클러 뷰에 바인딩될 아이탬 들의 초기 설정 값을 받는 용도
    protected abstract fun getDataBindingConfig(): DataBindingConfig

    // Create View Holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        Timber.d("onCreateViewHolder")
        // config load
        val dbConfig:DataBindingConfig = getDataBindingConfig()
        val binding: B = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            dbConfig.layout,
            parent,
            false
        )
        // if has ViewModel..add ViewModel..!
        binding.setVariable(dbConfig.vmVariableId, dbConfig.stateViewModel)
        // add other data binding params...
        val params: SparseArray<Any?> = dbConfig.bindingParams
        params.forEach { key, value ->
            binding.setVariable(key, value)
        }

        return BaseBindingViewHolder(binding.root)
    }

    // Bind ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = DataBindingUtil.getBinding<B>(holder.itemView)
        binding?.let { onBindItem(it, position, getItem(position), holder) }
        binding?.executePendingBindings()
    }

    // 데이터 바인딩에 의해 값이 세팅된 이후..!
    // 레이아웃이 구성된 이후에 추가적인 리스너 등을 동록할 때 사용
    protected abstract fun onBindItem(binding: B, position:Int, item: M, holder: RecyclerView.ViewHolder)

    class BaseBindingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}