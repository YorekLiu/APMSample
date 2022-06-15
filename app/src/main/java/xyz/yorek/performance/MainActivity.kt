package xyz.yorek.performance

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import xyz.yorek.performance.base.ViewBindingBaseActivity
import xyz.yorek.performance.databinding.ActivityMainBinding

class MainActivity : ViewBindingBaseActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater)
        initList()
    }

    private fun initList() {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        val activities = packageInfo.activities.filter {
            it.name != MainActivity::class.java.name && it.exported && it.loadLabel(packageManager) != "Leaks"
        }

        with (mBinding.recyclerView) {
            this.layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = MainAdapter(this@MainActivity, activities)
            this.setHasFixedSize(true)
        }
    }

    class MainAdapter(
        private val context: Context,
        private val activities: List<ActivityInfo>
    ): RecyclerView.Adapter<MainAdapter.Holder>() {

        private val mOnClickListener = View.OnClickListener {
            if (it.tag is ActivityInfo) {
                val activityName = (it.tag as ActivityInfo).name
                val intent = Intent()
                intent.component = ComponentName(context, activityName)
                context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val activityInfo = activities[position]
            holder.itemView.tag = activityInfo

            holder.textView.text = activityInfo.loadLabel(context.packageManager)
            holder.textView.setOnClickListener(mOnClickListener)
        }

        override fun getItemCount(): Int {
            return activities.size
        }

        class Holder(view: View): RecyclerView.ViewHolder(view) {
            val textView = view as TextView
        }
    }
}