function XUb(){}
function qAc(){}
function Jzc(){Kzc.call(this,false)}
function lAc(a,b){nAc.call(this,a,false);this.c=b}
function mAc(a,b){nAc.call(this,a,false);jAc(this,b)}
function kAc(a){nAc.call(this,'GWT',true);jAc(this,a)}
function YUb(a){this.d=a;this.c=gFb(this.d.b)}
function nzc(a,b){return uzc(a,b,a.b.c)}
function uzc(a,b,c){if(c<0||c>a.b.c){throw new TRc}a.p&&(b.R[C9c]=2,undefined);mzc(a,c,b.R);XYc(a.b,c,b);return b}
function jAc(a,b){a.e=b;!!a.d&&Izc(a.d,a);if(b){b.R.tabIndex=-1;a.R.setAttribute(Kad,U5c)}else{a.R.setAttribute(Kad,t7c)}}
function cFb(a){var b,c;b=Sgb(a.b.Zd(Cad),138);if(b==null){c=Igb(Kzb,{124:1,135:1,138:1},1,['New','Open',Dad,Ead,'Exit']);a.b._d(Cad,c);return c}else{return b}}
function bFb(a){var b,c;b=Sgb(a.b.Zd(Bad),138);if(b==null){c=Igb(Kzb,{124:1,135:1,138:1},1,['Undo','Redo','Cut','Copy','Paste']);a.b._d(Bad,c);return c}else{return b}}
function fFb(a){var b,c;b=Sgb(a.b.Zd(Had),138);if(b==null){c=Igb(Kzb,{124:1,135:1,138:1},1,['Contents','Fortune Cookie','About GWT']);a.b._d(Had,c);return c}else{return b}}
function eFb(a){var b,c;b=Sgb(a.b.Zd(Gad),138);if(b==null){c=Igb(Kzb,{124:1,135:1,138:1},1,['Download','Examples',C5c,"GWT wit' the program"]);a.b._d(Gad,c);return c}else{return b}}
function dFb(a){var b,c;b=Sgb(a.b.Zd(Fad),138);if(b==null){c=Igb(Kzb,{124:1,135:1,138:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b._d(Fad,c);return c}else{return b}}
function rAc(){var a;xe(this,$doc.createElement(j7c));this.R[X2c]='gwt-MenuItemSeparator';a=$doc.createElement(a3c);yl(this.R,FCc(a));a[X2c]='menuSeparatorInner'}
function gFb(a){var b,c;b=Sgb(a.b.Zd(Iad),138);if(b==null){c=Igb(Kzb,{124:1,135:1,138:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b._d(Iad,c);return c}else{return b}}
function TUb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new YUb(a);n=new Jzc;n.c=true;n.R.style[Y2c]='500px';n.f=true;q=new Kzc(true);p=dFb(a.b);for(k=0;k<p.length;++k){lzc(q,new lAc(p[k],o))}d=new Kzc(true);d.f=true;lzc(n,new mAc('File',d));e=cFb(a.b);for(k=0;k<e.length;++k){if(k==3){nzc(d,new rAc);lzc(d,new mAc(e[3],q));nzc(d,new rAc)}else{lzc(d,new lAc(e[k],o))}}b=new Kzc(true);lzc(n,new mAc('Edit',b));c=bFb(a.b);for(k=0;k<c.length;++k){lzc(b,new lAc(c[k],o))}f=new Kzc(true);lzc(n,new kAc(f));g=eFb(a.b);for(k=0;k<g.length;++k){lzc(f,new lAc(g[k],o))}i=new Kzc(true);nzc(n,new rAc);lzc(n,new mAc('Help',i));j=fFb(a.b);for(k=0;k<j.length;++k){lzc(i,new lAc(j[k],o))}VJc(n.R,V2c,Jad);Hzc(n,Jad);return n}
var Kad='aria-haspopup',Jad='cwMenuBar',Bad='cwMenuBarEditOptions',Cad='cwMenuBarFileOptions',Fad='cwMenuBarFileRecents',Gad='cwMenuBarGWTOptions',Had='cwMenuBarHelpOptions',Iad='cwMenuBarPrompts';_=YUb.prototype=XUb.prototype=new Y;_.hc=function ZUb(){plc(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function $Ub(){return rpb};_.cM={82:1};_.b=0;_.d=null;_=_Ub.prototype;_.fc=function dVb(){EDb(this.c,TUb(this.b))};_=Jzc.prototype=kzc.prototype;_=mAc.prototype=lAc.prototype=kAc.prototype=fAc.prototype;_=rAc.prototype=qAc.prototype=new se;_.gC=function sAc(){return Xub};_.cM={91:1,97:1,110:1};var rpb=ASc(s8c,'CwMenuBar$1'),Xub=ASc(W7c,'MenuItemSeparator');T2c(Hj)(18);