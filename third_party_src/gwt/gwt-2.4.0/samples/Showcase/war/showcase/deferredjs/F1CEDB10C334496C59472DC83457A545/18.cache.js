function odb(){}
function JUb(){}
function aUb(){bUb.call(this,false)}
function EUb(a,b){GUb.call(this,a,false);this.c=b}
function FUb(a,b){GUb.call(this,a,false);CUb(this,b)}
function DUb(a){GUb.call(this,'GWT',true);CUb(this,a)}
function pdb(a){this.d=a;this.c=zZ(this.d.b)}
function FTb(a,b){return NTb(a,b,a.b.c)}
function NTb(a,b,c){if(c<0||c>a.b.c){throw new qac}a.p&&(b.R[ytc]=2,undefined);ETb(a,c,b.R);uhc(a.b,c,b);return b}
function CUb(a,b){a.e=b;!!a.d&&_Tb(a.d,a);if(b){(JQb(),b.R).tabIndex=-1;a.R.setAttribute(Buc,Ypc)}else{a.R.setAttribute(Buc,qrc)}}
function vZ(a){var b,c;b=KC(a.b.dd(tuc),137);if(b==null){c=AC(bU,{123:1,134:1,137:1},1,['New','Open',uuc,vuc,'Exit']);a.b.fd(tuc,c);return c}else{return b}}
function uZ(a){var b,c;b=KC(a.b.dd(suc),137);if(b==null){c=AC(bU,{123:1,134:1,137:1},1,['Undo','Redo','Cut','Copy','Paste']);a.b.fd(suc,c);return c}else{return b}}
function yZ(a){var b,c;b=KC(a.b.dd(yuc),137);if(b==null){c=AC(bU,{123:1,134:1,137:1},1,['Contents','Fortune Cookie','About GWT']);a.b.fd(yuc,c);return c}else{return b}}
function xZ(a){var b,c;b=KC(a.b.dd(xuc),137);if(b==null){c=AC(bU,{123:1,134:1,137:1},1,['Download','Examples',Gpc,"GWT wit' the program"]);a.b.fd(xuc,c);return c}else{return b}}
function wZ(a){var b,c;b=KC(a.b.dd(wuc),137);if(b==null){c=AC(bU,{123:1,134:1,137:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.fd(wuc,c);return c}else{return b}}
function KUb(){var a;ye(this,$doc.createElement(grc));this.R[unc]='gwt-MenuItemSeparator';a=$doc.createElement(znc);Sl(this.R,YWb(a));a[unc]='menuSeparatorInner'}
function zZ(a){var b,c;b=KC(a.b.dd(zuc),137);if(b==null){c=AC(bU,{123:1,134:1,137:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.fd(zuc,c);return c}else{return b}}
function kdb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new pdb(a);n=new aUb;n.c=true;n.R.style[vnc]='500px';n.f=true;q=new bUb(true);p=wZ(a.b);for(k=0;k<p.length;++k){DTb(q,new EUb(p[k],o))}d=new bUb(true);d.f=true;DTb(n,new FUb('File',d));e=vZ(a.b);for(k=0;k<e.length;++k){if(k==3){FTb(d,new KUb);DTb(d,new FUb(e[3],q));FTb(d,new KUb)}else{DTb(d,new EUb(e[k],o))}}b=new bUb(true);DTb(n,new FUb('Edit',b));c=uZ(a.b);for(k=0;k<c.length;++k){DTb(b,new EUb(c[k],o))}f=new bUb(true);DTb(n,new DUb(f));g=xZ(a.b);for(k=0;k<g.length;++k){DTb(f,new EUb(g[k],o))}i=new bUb(true);FTb(n,new KUb);DTb(n,new FUb('Help',i));j=yZ(a.b);for(k=0;k<j.length;++k){DTb(i,new EUb(j[k],o))}m2b(n.R,snc,Auc);$Tb(n,Auc);return n}
var Buc='aria-haspopup',Auc='cwMenuBar',suc='cwMenuBarEditOptions',tuc='cwMenuBarFileOptions',wuc='cwMenuBarFileRecents',xuc='cwMenuBarGWTOptions',yuc='cwMenuBarHelpOptions',zuc='cwMenuBarPrompts';_=pdb.prototype=odb.prototype=new Y;_.hc=function qdb(){GFb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function rdb(){return IJ};_.cM={81:1};_.b=0;_.d=null;_=sdb.prototype;_.fc=function wdb(){XX(this.c,kdb(this.b))};_=aUb.prototype=CTb.prototype;_=FUb.prototype=EUb.prototype=DUb.prototype=yUb.prototype;_=KUb.prototype=JUb.prototype=new te;_.gC=function LUb(){return mP};_.cM={90:1,96:1,109:1};var IJ=Zac(psc,'CwMenuBar$1'),mP=Zac(Trc,'MenuItemSeparator');qnc(Hj)(18);