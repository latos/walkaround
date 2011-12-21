function NUb(){}
function nAc(){}
function Gzc(){Hzc.call(this,false)}
function iAc(a,b){kAc.call(this,a,false);this.b=b}
function jAc(a,b){kAc.call(this,a,false);gAc(this,b)}
function hAc(a){kAc.call(this,'GWT',true);gAc(this,a)}
function OUb(a){this.c=a;this.b=YEb(this.c.a)}
function kzc(a,b){return rzc(a,b,a.a.b)}
function rzc(a,b,c){if(c<0||c>a.a.b){throw new PRc}a.o&&(b.Q[r9c]=2,undefined);jzc(a,c,b.Q);TYc(a.a,c,b);return b}
function gAc(a,b){a.d=b;!!a.c&&Fzc(a.c,a);if(b){b.Q.tabIndex=-1;a.Q.setAttribute(zad,S5c)}else{a.Q.setAttribute(zad,f7c)}}
function oAc(){var a;ke(this,Kl($doc,X6c));this.Q[T2c]='gwt-MenuItemSeparator';a=Kl($doc,Z2c);jl(this.Q,CCc(a));a[T2c]='menuSeparatorInner'}
function UEb(a){var b,c;b=Egb(a.a.Vd(rad),138);if(b==null){c=ugb(uzb,{124:1,135:1,138:1},1,['New','Open',sad,tad,'Exit']);a.a.Xd(rad,c);return c}else{return b}}
function TEb(a){var b,c;b=Egb(a.a.Vd(qad),138);if(b==null){c=ugb(uzb,{124:1,135:1,138:1},1,['Undo','Redo','Cut','Copy','Paste']);a.a.Xd(qad,c);return c}else{return b}}
function XEb(a){var b,c;b=Egb(a.a.Vd(wad),138);if(b==null){c=ugb(uzb,{124:1,135:1,138:1},1,['Contents','Fortune Cookie','About GWT']);a.a.Xd(wad,c);return c}else{return b}}
function WEb(a){var b,c;b=Egb(a.a.Vd(vad),138);if(b==null){c=ugb(uzb,{124:1,135:1,138:1},1,['Download','Examples',x5c,"GWT wit' the program"]);a.a.Xd(vad,c);return c}else{return b}}
function VEb(a){var b,c;b=Egb(a.a.Vd(uad),138);if(b==null){c=ugb(uzb,{124:1,135:1,138:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.a.Xd(uad,c);return c}else{return b}}
function YEb(a){var b,c;b=Egb(a.a.Vd(xad),138);if(b==null){c=ugb(uzb,{124:1,135:1,138:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.a.Xd(xad,c);return c}else{return b}}
function JUb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new OUb(a);n=new Gzc;n.b=true;n.Q.style[U2c]='500px';n.e=true;q=new Hzc(true);p=VEb(a.a);for(k=0;k<p.length;++k){izc(q,new iAc(p[k],o))}d=new Hzc(true);d.e=true;izc(n,new jAc('File',d));e=UEb(a.a);for(k=0;k<e.length;++k){if(k==3){kzc(d,new oAc);izc(d,new jAc(e[3],q));kzc(d,new oAc)}else{izc(d,new iAc(e[k],o))}}b=new Hzc(true);izc(n,new jAc('Edit',b));c=TEb(a.a);for(k=0;k<c.length;++k){izc(b,new iAc(c[k],o))}f=new Hzc(true);izc(n,new hAc(f));g=WEb(a.a);for(k=0;k<g.length;++k){izc(f,new iAc(g[k],o))}i=new Hzc(true);kzc(n,new oAc);izc(n,new jAc('Help',i));j=XEb(a.a);for(k=0;k<j.length;++k){izc(i,new iAc(j[k],o))}UJc(n.Q,R2c,yad);Ezc(n,yad);return n}
var zad='aria-haspopup',yad='cwMenuBar',qad='cwMenuBarEditOptions',rad='cwMenuBarFileOptions',uad='cwMenuBarFileRecents',vad='cwMenuBarGWTOptions',wad='cwMenuBarHelpOptions',xad='cwMenuBarPrompts';_=OUb.prototype=NUb.prototype=new Y;_.cc=function PUb(){slc(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.gC=function QUb(){return apb};_.cM={82:1};_.a=0;_.c=null;_=RUb.prototype;_.ac=function VUb(){uDb(this.b,JUb(this.a))};_=Gzc.prototype=hzc.prototype;_=jAc.prototype=iAc.prototype=hAc.prototype=cAc.prototype;_=oAc.prototype=nAc.prototype=new fe;_.gC=function pAc(){return Hub};_.cM={91:1,97:1,110:1};var apb=wSc(f8c,'CwMenuBar$1'),Hub=wSc(J7c,'MenuItemSeparator');P2c(sj)(18);