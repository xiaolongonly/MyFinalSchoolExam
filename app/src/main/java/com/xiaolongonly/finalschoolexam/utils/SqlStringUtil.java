package com.xiaolongonly.finalschoolexam.utils;

import android.util.Log;

import com.xiaolongonly.finalschoolexam.model.ChatModel;
import com.xiaolongonly.finalschoolexam.model.TaskModel;
import com.xiaolongonly.finalschoolexam.model.UserModel;

/**
 * Created by GuoXiaolong on 2016/3/30.
 */
public class SqlStringUtil {
    private final static String TAG="SqlStringUtil";
    /**
     * 插入条数据，在创建任务的时候往任务表插数据
     *
     * @param taskModel
     * @return
     */
    public static String insertIntoTableTask(TaskModel taskModel) {
        String sql = "insert into task ( publisher_id , task_title , task_content ," +
                " task_locationx , task_locationy ,task_createtime , task_finishtime , task_location) " +
                "values( " + appendValue(String.valueOf(taskModel.getPublisher_id())) + " ," + appendValue(taskModel.getTask_title()) + " ," + appendValue(taskModel.getTask_content()) + " ," + appendValue(taskModel.getTask_locationx()) +
                " ," + appendValue(taskModel.getTask_locationy()) + " ," + appendValue(taskModel.getTask_createtime()) + " ," + appendValue(taskModel.getTask_finishtime())+ " ," + appendValue(taskModel.getTask_location()) + " )";
        return sql;
    }
//    public static String insertIntoTableTask(TaskModel taskModel) {
//        String sql = "insert into task ( publisher_id , picker_id , task_title , task_content ," +
//                " task_locationx , task_locationy , task_endtime , task_statu ) " +
//                "values( " + appendValue(String.valueOf(taskModel.getPublisher_id())) + " ," + appendValue(String.valueOf(taskModel.getPicker_id())) +
//                " ," + appendValue(taskModel.getTask_title()) + " ," + appendValue(taskModel.getTask_content()) + " ," + appendValue(taskModel.getTask_locationx()) +
//                " ," + appendValue(taskModel.getTask_locationy()) + " ," + appendValue(taskModel.getTask_endtime()) + " ," + appendValue(taskModel.getTask_statu()) + " )";
//        return sql;
//    }

    /**
     * 通过表名查找全部数据
     *
     * @param tablename
     * @return
     */
    public static String selectAllFromTable(String tablename) {
        return "select * from " + tablename;
    }

    public static String insertIntoTableUser(UserModel userModel) {
        String sql = "insert into user ( user_account , user_password , user_tel , user_qq ," +
                " user_name )" + "values (" + appendValue(userModel.getUser_account()) + " ," + appendValue(userModel.getUser_password()) + " ," +
                appendValue(String.valueOf(userModel.getUser_tel())) + " ," + appendValue(String.valueOf(userModel.getUser_qq())) + " ," +
                appendValue(userModel.getUser_name())  + " )";
        Log.i(TAG,sql);
        return sql;
    }

    /**
     * 通过账号和密码获取用户信息
     *
     * @param account
     * @param psw
     * @return
     */
    public static String getuserInfoByAP(String account, String psw) {
        String sql = "select * from user where user_account =" + appendValue(account) + "and user_password =" + appendValue(psw);
        return sql;
    }

    /**
     * @param value
     * @return
     */
    private static String appendValue(String value) {
        return " '" + value + "'";
    }

    /**
     * 通过userid来获取用户信息
     *
     * @param userid
     * @return
     */
    public static String getuserInfoByUserid(int userid) {
        String sql = "select * from user where user_id =" + appendValue(String.valueOf(userid));
        return sql;
    }

    /**
     * 修改这个任务id下的任务状态
     * @param task_id
     * @param task_statu statu:1.未接取，2.已接取，3.已关闭，4.已完成
     * @return
     */
    public static String modifyTaskStatu(int task_id, int task_statu,String task_finishtime) {
        String sql = "update task set task_statu =" + appendValue(String.valueOf(task_statu)) +", task_finishtime ="+appendValue(task_finishtime)+ "where task_id=" + appendValue(String.valueOf(task_id));
        return sql;
    }

    /**
     * 修改这个任务id下的接取者id
     * @param task_id
     * @param picker_id
     * @return
     */
    public static String modifySP(int task_id, int task_statu,int picker_id) {
        String sql = "update task set picker_id =" + appendValue(String.valueOf(picker_id))+", task_statu ="+ appendValue(String.valueOf(task_statu)) + "where task_id=" + appendValue(String.valueOf(task_id));
        return sql;
    }

    /**
     * 获取已接取的任务列表
     *
     * @param picker_id
     * @param level     status 0:all 1:未接取 2：已接取 3：已完成 已关闭
     * @return
     */
    public static String getTaskListByPickerid(int picker_id, int level) {
        String sql;
        if (level == 5) {
            sql = "select * from task where picker_id = " + appendValue(String.valueOf(picker_id));
            return sql;
        }
        sql = "select * from task where picker_id = " + appendValue(String.valueOf(picker_id)) + " and task_statu =" + appendValue(String.valueOf(level));
        return sql;
    }

    /**
     * 获取当前用户已发布的任务列表
     * @param publisher_id
     * @param level
     * @return
     */
    public static String getTaskListByPublisherid(int publisher_id,int level)
    {
        String sql;
        if (level == 5) {
            sql = "select * from task where publisher_id = " + appendValue(String.valueOf(publisher_id));
            return sql;
        }
        sql = "select * from task where publisher_id = " + appendValue(String.valueOf(publisher_id)) + " and task_statu =" + appendValue(String.valueOf(level));
        return sql;
    }


    /**
     * 修改当前user_id下的用户名字、电话、QQ
     *
     * @param user_id
     * @param user_name
     * @param user_tel
     * @param user_qq
     * @return
     */
    public static String modifyUserInfo(int user_id, String user_name, String user_tel, String user_qq ,String user_imageurl) {
        String sql = "update user set user_name = " + appendValue(user_name) + " ," + "user_tel =" + appendValue(user_tel) + " ," + "user_qq =" + appendValue(user_qq) +
                " ," + "user_imageurl =" + appendValue(user_imageurl) +"where user_id = " + appendValue(String.valueOf(user_id));
        return sql;
    }

    /**
     * 更新当前用户位置信息
     * @param user_id
     * @param locx
     * @param locy
     * @return
     */
    public static String modifyUserLoc(int user_id,double locx,double locy)
    {
        String sql="update user set def_locx = " + locx + " , " + "def_locy = " + locy +" where user_id = " + user_id;
        return sql;
    }
    /**
     * 通过id，用户旧密码，用户新密码来设置密码
     *
     * @param user_id
     * @param user_password
     * @param new_password
     * @return
     */
    public static String modifyUserPswByIP(int user_id, String user_password, String new_password) {
        String sql = "update user set user_password =" + appendValue(new_password) + " where user_id = " +
                appendValue(String.valueOf(user_id)) + " and user_password =" + appendValue(user_password);
        return sql;
    }

    /**
     * 通过任务id获取单条任务信息
     * @param task_id
     * @return
     */
    public static String getTaskInfoByTaskid(int task_id)
    {
        String sql = "select * from task where task_id =" + String.valueOf(task_id);
        return sql;
    }
    /**
     * 根据任务的状态来获取任务
     * @param level
     * @return  0就是当前用户可接取的列表,1就是当前未关闭/完成的任务列表
     */
    public static String getTaskList(int level) {
        String sql = "select * from task where task_statu <=" + String.valueOf(level);
        return sql;
    }

//    /**
//     *
//     * @param type  0查询 1插入 2更新
//     * @param tablename
//     * @param where 查所有就0=0
//     * @param rows 插入表的剩下的语句( publisher_id , picker_id , task_title , task_content ,task_locationx , task_locationy , task_endtime , task_statu ) values('1','2','3','4','5','6','7','8')
//     * @return
//     */
//    private static String usefulSql(int type,String tablename ,String where,String rows)
//    {
//        String sql="";
//        switch (type)
//        {
//            case 0:
//            sql="select "+ rows + "from" +tablename + " where "+where;
//                break;
//            case 1:
//                sql = "insert into "+tablename+rows;
//                break;
//            case 2:
//                sql= "update "+tablename+ " set " + rows +where;
//        }
    public static String isAccountRegister(String user_account)
    {
        String sql = "select * from user where user_account =" + appendValue(user_account);
        return sql;
    }

    /**
     * 有数目限制的SQL语句
     * @param page
     * @param pagesize
     * @return
     */
    public static String pageindex(String sql,int page ,int pagesize)
    {
        String pageindexsql="select * , (select count(*)"+sql.substring(8)+") as total from ("+sql+") as result limit " +((page-1)*pagesize)+" ,"+pagesize;
        Log.i(TAG, pageindexsql);
        return pageindexsql;
    }

    /**
     * 插入聊天数据。
     * @param chatModel
     * @return
     */
    public static String insertIntoTableChat(ChatModel chatModel)
    {
        String sql = "insert into chat ( fromuser_id , touser_id , message , receive_time) " +
                "values( " + appendValue(String.valueOf(chatModel.getFromuser_id())) + " ," + appendValue(chatModel.getTouser_id()) + " ," + appendValue(chatModel.getMessage()) + " ," + appendValue(chatModel.getReceive_time()) + " )";
        return sql;
    }

    /**
     * 获取与两个用户之间的聊天数据信息
     * @param fromUserId
     * @param toUserId
     * @return
     */
    public static String getChatMessageData(String fromUserId,String toUserId)
    {
        String sql = "select * from chat where (fromuser_id = " +fromUserId +" and touser_id = "+toUserId+") or (fromuser_id = " +toUserId +" and touser_id = "+fromUserId+")";
        return sql;
    }

    /**
     * 除当前用户外所有用户
     * @param user_id //当前用户id
     * @return
     */
    public static String selectOtherUser(String user_id) {
        return "select * from user where user_id not in ( " + user_id +" )";
    }
}
