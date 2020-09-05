package ru.krus.ufanews;

public class News {
    String title;
    String descr;
    String additional;
    String image;
    String link;

    News(String _title, String _descr, String _additional, String _image, String _link){
        title = _title;
        descr = _descr;
        additional = _additional;
        image = _image;
        link = _link;
    }
}
