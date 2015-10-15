package com.jingtao.app;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jingtao.app.main_page_list_view.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by jingtao on 15-10-15.
 */
public class SaveQuestion {
    private final Model question;
    private String path;

    public SaveQuestion(Context context, Model question) {
        this.question=question;
        this.path=Environment.getExternalStorageDirectory().getPath() + "/easy_ace/student_saved_question";
        File save_file = new File(path);
        try {
            if (!save_file.exists()) {
                save_file.createNewFile();
            }
        }catch (Exception e){
            Log.e("Error",e.toString());
        }
    }
    public SaveQuestion(Context context) {
        question=null;
        this.path=Environment.getExternalStorageDirectory().getPath() + "/easy_ace/student_saved_question";
        File save_file = new File(path);
        try {
            if (!save_file.exists()) {
                save_file.createNewFile();
            }
        }catch (Exception e){
            Log.e("Error",e.toString());
        }
    }
    public ArrayList<Model> ReadSavedQuestion(){
        try {
            ArrayList<Model> savedQuestionList =new ArrayList<>();
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            savedQuestionList = (ArrayList<Model>) in.readObject();
            in.close();
            fileIn.close();
            return savedQuestionList;
        }catch (Exception e){
            Log.e("error", e.toString());
        }
        return null;
    }
    public boolean checksaved(){
        ArrayList<Model> savedQuestionList = new ArrayList<>();
        savedQuestionList=ReadSavedQuestion();
        if(savedQuestionList==null) return false;
        for(Model qst:savedQuestionList){
            if(qst.getId().equals(question.getId())) return true;
        }
        return false;
    }
    public boolean checksaved(ArrayList<Model> questions){
        for(Model qst:questions){
            if(qst.getId().equals(question.getId())) return true;
        }
        return false;
    }
    public boolean save(){
        try {
            File save_file = new File(path);
            ArrayList<Model> savedQuestionList =new ArrayList<>();
            try{
                FileInputStream fileIn = new FileInputStream(path);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                savedQuestionList = (ArrayList<Model>) in.readObject();
                in.close();
                fileIn.close();
            }catch (Exception e){
                Log.e("error read saving",e.toString());
                e.printStackTrace();
            }
            if(!checksaved(savedQuestionList)) {
                savedQuestionList.add(question);
            }
            FileOutputStream fout = new FileOutputStream(save_file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(savedQuestionList);
            fout.close();
            oos.close();
            return true;
        }catch(Exception e){
            Log.e("error",e.toString());
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(){
        try {
            File save_file = new File(path);
            if(!save_file.exists()){
                save_file.createNewFile();
            }
            ArrayList<Model> savedQuestionList =new ArrayList<>();
            ArrayList<Model> newSavedList = new ArrayList<>();
            try{
                FileInputStream fileIn = new FileInputStream(path);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                savedQuestionList = (ArrayList<Model>) in.readObject();
                in.close();
                fileIn.close();
            }catch (Exception e){
                Log.e("error read saving",e.toString());
                e.printStackTrace();
            }
            for(Model qst:savedQuestionList){
                if(!(qst.getId().equals(question.getId()))){
                    newSavedList.add(qst);
                }
            }
            FileOutputStream fout = new FileOutputStream(save_file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(newSavedList);
            fout.close();
            oos.close();
            return true;
        }catch(Exception e){
            Log.e("error",e.toString());
            e.printStackTrace();
        }
        return false;
    }
}
