package com.sheepion.exppool.listener;

//保存经验池所在的世界和两顶点坐标
public record Pool(String worldName,int x1, int y1, int z1, int x2, int y2, int z2,int exp) {

}
