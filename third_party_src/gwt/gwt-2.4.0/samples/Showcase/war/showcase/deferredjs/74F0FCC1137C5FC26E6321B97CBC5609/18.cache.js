function jVb(){}
function EAc(){}
function Xzc(){Yzc.call(this,false)}
function zAc(a,b){BAc.call(this,a,false);this.c=b}
function AAc(a,b){BAc.call(this,a,false);xAc(this,b)}
function yAc(a){BAc.call(this,'GWT',true);xAc(this,a)}
function kVb(a){this.d=a;this.c=uFb(this.d.b)}
function Azc(a,b){return Izc(a,b,a.b.c)}
function Izc(a,b,c){if(c<0||c>a.b.c){throw new lSc}a.p&&(b.R[T9c]=2,undefined);zzc(a,c,b.R);pZc(a.b,c,b);return b}
function xAc(a,b){a.e=b;!!a.d&&Wzc(a.d,a);if(b){(Ewc(),b.R).tabIndex=-1;a.R.setAttribute(_ad,r6c)}else{a.R.setAttribute(_ad,L7c)}}
function qFb(a){var b,c;b=_gb(a.b.be(Tad),138);if(b==null){c=Rgb(Yzb,{124:1,135:1,138:1},1,['New','Open',Uad,Vad,'Exit']);a.b.de(Tad,c);return c}else{return b}}
function pFb(a){var b,c;b=_gb(a.b.be(Sad),138);if(b==null){c=Rgb(Yzb,{124:1,135:1,138:1},1,['Undo','Redo','Cut','Copy','Paste']);a.b.de(Sad,c);return c}else{return b}}
function tFb(a){var b,c;b=_gb(a.b.be(Yad),138);if(b==null){c=Rgb(Yzb,{124:1,135:1,138:1},1,['Contents','Fortune Cookie','About GWT']);a.b.de(Yad,c);return c}else{return b}}
function sFb(a){var b,c;b=_gb(a.b.be(Xad),138);if(b==null){c=Rgb(Yzb,{124:1,135:1,138:1},1,['Download','Examples',_5c,"GWT wit' the program"]);a.b.de(Xad,c);return c}else{return b}}
function rFb(a){var b,c;b=_gb(a.b.be(Wad),138);if(b==null){c=Rgb(Yzb,{124:1,135:1,138:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.de(Wad,c);return c}else{return b}}
function FAc(){var a;ye(this,$doc.createElement(B7c));this.R[p3c]='gwt-MenuItemSeparator';a=$doc.createElement(u3c);Sl(this.R,TCc(a));a[p3c]='menuSeparatorInner'}
function uFb(a){var b,c;b=_gb(a.b.be(Zad),138);if(b==null){c=Rgb(Yzb,{124:1,135:1,138:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.de(Zad,c);return c}else{return b}}
function fVb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new kVb(a);n=new Xzc;n.c=true;n.R.style[q3c]='500px';n.f=true;q=new Yzc(true);p=rFb(a.b);for(k=0;k<p.length;++k){yzc(q,new zAc(p[k],o))}d=new Yzc(true);d.f=true;yzc(n,new AAc('File',d));e=qFb(a.b);for(k=0;k<e.length;++k){if(k==3){Azc(d,new FAc);yzc(d,new AAc(e[3],q));Azc(d,new FAc)}else{yzc(d,new zAc(e[k],o))}}b=new Yzc(true);yzc(n,new AAc('Edit',b));c=pFb(a.b);for(k=0;k<c.length;++k){yzc(b,new zAc(c[k],o))}f=new Yzc(true);yzc(n,new yAc(f));g=sFb(a.b);for(k=0;k<g.length;++k){yzc(f,new zAc(g[k],o))}i=new Yzc(true);Azc(n,new FAc);yzc(n,new AAc('Help',i));j=tFb(a.b);for(k=0;k<j.length;++k){yzc(i,new zAc(j[k],o))}hKc(n.R,n3c,$ad);Vzc(n,$ad);return n}
var _ad='aria-haspopup',$ad='cwMenuBar',Sad='cwMenuBarEditOptions',Tad='cwMenuBarFileOptions',Wad='cwMenuBarFileRecents',Xad='cwMenuBarGWTOptions',Yad='cwMenuBarHelpOptions',Zad='cwMenuBarPrompts';_=kVb.prototype=jVb.prototype=new Y;_.hc=function lVb(){Blc(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function mVb(){return Dpb};_.cM={82:1};_.b=0;_.d=null;_=nVb.prototype;_.fc=function rVb(){SDb(this.c,fVb(this.b))};_=Xzc.prototype=xzc.prototype;_=AAc.prototype=zAc.prototype=yAc.prototype=tAc.prototype;_=FAc.prototype=EAc.prototype=new te;_.gC=function GAc(){return hvb};_.cM={91:1,97:1,110:1};var Dpb=USc(K8c,'CwMenuBar$1'),hvb=USc(m8c,'MenuItemSeparator');l3c(Hj)(18);