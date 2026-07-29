package com.aeonvision.hud;

import com.aeonvision.keybind.KeyBindManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import java.util.*;

public class RadialHudScreen extends Screen {
    private static final MinecraftClient MC = MinecraftClient.getInstance();
    
    private static class Sector {
        String name, icon; int cx, cy; float sa, ea; boolean hov; Runnable act;
        Sector(String n, String i, Runnable a){name=n;icon=i;act=a;}
    }
    
    private List<Sector> sectors = new ArrayList<>();
    private int cx, cy, outer=90, inner=30;
    private float time=0;

    public RadialHudScreen(){super(Text.literal("Æon Vision HUD"));}

    @Override
    protected void init(){
        cx=width/2; cy=height/2;
        sectors.clear();
        sectors.add(new Sector("Косметика","✦",()->MC.setScreen(new com.aeonvision.ui.CosmeticScreen())));
        sectors.add(new Sector("Утилиты","⚙",()->MC.setScreen(new com.aeonvision.ui.UtilsScreen())));
        sectors.add(new Sector("Визуалы","◈",()->MC.setScreen(new com.aeonvision.ui.VisualsScreen())));
        sectors.add(new Sector("Миры","⬡",()->MC.setScreen(new com.aeonvision.ui.WorldManagerScreen())));
        sectors.add(new Sector("Серверы","⬢",()->MC.setScreen(new com.aeonvision.ui.ServerManagerScreen())));
        sectors.add(new Sector("Аккаунты","◉",()->MC.setScreen(new com.aeonvision.accounts.AccountManagerScreen())));
        float aps=360f/sectors.size();
        for(int i=0;i<sectors.size();i++){Sector s=sectors.get(i); s.cx=cx; s.cy=cy; s.sa=i*aps-90; s.ea=(i+1)*aps-90;}
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float d){
        time+=d;
        ctx.fill(0,0,width,height,0x80000000);
        for(Sector s:sectors){s.hov=inSector(mx,my,s); drawSector(ctx,s);}
        drawCenter(ctx);
        Sector hov=sectors.stream().filter(s->s.hov).findFirst().orElse(null);
        if(hov!=null){
            ctx.drawText(textRenderer,Text.literal(hov.name),cx-textRenderer.getWidth(hov.name)/2,cy+outer+20,0xFFFFFFFF,true);
            ctx.drawText(textRenderer,Text.literal(hov.icon),cx-textRenderer.getWidth(hov.icon)/2,cy-60,0xFFFFFFFF,true);
        }
        String ins="Наведи на сектор и отпусти Shift";
        ctx.drawText(textRenderer,Text.literal(ins),cx-textRenderer.getWidth(ins)/2,height-30,0x60FFFFFF,false);
    }

    private void drawSector(DrawContext ctx, Sector s){
        int seg=32; float ma=(s.sa+s.ea)/2, ia=(float)Math.toRadians(ma);
        int bc=s.hov?0x60FFFFFF:0x20FFFFFF, lc=s.hov?0xFFFFFFFF:0x40FFFFFF;
        for(int i=0;i<seg;i++){
            float a1=(float)Math.toRadians(s.sa+(s.ea-s.sa)*i/seg), a2=(float)Math.toRadians(s.sa+(s.ea-s.sa)*(i+1)/seg);
            int x1=cx+(int)(Math.cos(a1)*inner), y1=cy+(int)(Math.sin(a1)*inner);
            int x2=cx+(int)(Math.cos(a1)*outer), y2=cy+(int)(Math.sin(a1)*outer);
            int x3=cx+(int)(Math.cos(a2)*outer), y3=cy+(int)(Math.sin(a2)*outer);
            int x4=cx+(int)(Math.cos(a2)*inner), y4=cy+(int)(Math.sin(a2)*inner);
            fillTri(ctx,x1,y1,x2,y2,x3,y3,bc); fillTri(ctx,x1,y1,x3,y3,x4,y4,bc);
        }
        int id=(inner+outer)/2, ix=cx+(int)(Math.cos(ia)*id), iy=cy+(int)(Math.sin(ia)*id);
        ctx.drawText(textRenderer,Text.literal(s.icon),ix-textRenderer.getWidth(s.icon)/2,iy-textRenderer.fontHeight/2,s.hov?0xFFFFFFFF:0xA0FFFFFF,false);
        // Линии через fill вместо drawLine
        int bx1=cx+(int)(Math.cos(Math.toRadians(s.sa))*outer), by1=cy+(int)(Math.sin(Math.toRadians(s.sa))*outer);
        drawLineFill(ctx,cx,cy,bx1,by1,lc);
        int bx2=cx+(int)(Math.cos(Math.toRadians(s.ea))*outer), by2=cy+(int)(Math.sin(Math.toRadians(s.ea))*outer);
        drawLineFill(ctx,cx,cy,bx2,by2,lc);
    }

    private void drawLineFill(DrawContext ctx, int x1, int y1, int x2, int y2, int color){
        int dx=Math.abs(x2-x1), dy=Math.abs(y2-y1), sx=x1<x2?1:-1, sy=y1<y2?1:-1, err=dx-dy;
        while(true){
            ctx.fill(x1,y1,x1+1,y1+1,color);
            if(x1==x2&&y1==y2)break;
            int e2=2*err;
            if(e2>-dy){err-=dy; x1+=sx;}
            if(e2<dx){err+=dx; y1+=sy;}
        }
    }

    private void drawCenter(DrawContext ctx){
        drawCircle(ctx,cx,cy,inner,0x40FFFFFF);
        String logo="Æ"; ctx.drawText(textRenderer,Text.literal(logo),cx-textRenderer.getWidth(logo)/2,cy-textRenderer.fontHeight/2,0xFFFFFFFF,false);
        float pulse=1+MathHelper.sin(time*3f)*0.1f;
        drawCircle(ctx,cx,cy,(int)(inner*pulse),0x30FFFFFF);
    }

    private boolean inSector(int mx, int my, Sector s){
        float dx=mx-cx, dy=my-cy, dist=(float)Math.sqrt(dx*dx+dy*dy);
        if(dist<inner||dist>outer)return false;
        float ang=(float)Math.toDegrees(Math.atan2(dy,dx)); if(ang<-90)ang+=360;
        float st=s.sa, en=s.ea;
        return st>en?ang>=st||ang<=en:ang>=st&&ang<=en;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn){
        for(Sector s:sectors)if(inSector((int)mx,(int)my,s)){s.act.run(); close(); return true;}
        close(); return true;
    }

    @Override public void close(){super.close(); KeyBindManager.closeHudPanel2();}
    @Override public boolean shouldPause(){return false;}

    private void fillTri(DrawContext ctx, int x1, int y1, int x2, int y2, int x3, int y3, int c){
        int mnX=Math.min(x1,Math.min(x2,x3)), mxX=Math.max(x1,Math.max(x2,x3));
        int mnY=Math.min(y1,Math.min(y2,y3)), mxY=Math.max(y1,Math.max(y2,y3));
        for(int x=mnX;x<=mxX;x++)for(int y=mnY;y<=mxY;y++)if(ptInTri(x,y,x1,y1,x2,y2,x3,y3))ctx.fill(x,y,x+1,y+1,c);
    }

    private boolean ptInTri(int px,int py,int x1,int y1,int x2,int y2,int x3,int y3){
        float d1=sign(px,py,x1,y1,x2,y2), d2=sign(px,py,x2,y2,x3,y3), d3=sign(px,py,x3,y3,x1,y1);
        boolean hn=(d1<0)||(d2<0)||(d3<0), hp=(d1>0)||(d2>0)||(d3>0);
        return!(hn&&hp);
    }

    private float sign(int px,int py,int x1,int y1,int x2,int y2){return(px-x2)*(y1-y2)-(x1-x2)*(py-y2);}

    private void drawCircle(DrawContext ctx, int cx, int cy, int r, int c){
        for(int x=-r;x<=r;x++)for(int y=-r;y<=r;y++){int d=x*x+y*y; if(d<=r*r&&d>=(r-1)*(r-1))ctx.fill(cx+x,cy+y,cx+x+1,cy+y+1,c);}
    }
                }
