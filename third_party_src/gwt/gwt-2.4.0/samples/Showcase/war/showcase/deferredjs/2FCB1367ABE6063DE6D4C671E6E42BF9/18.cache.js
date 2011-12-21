function adb(){}
function vUb(){}
function OTb(){PTb.call(this,false)}
function qUb(a,b){sUb.call(this,a,false);this.c=b}
function rUb(a,b){sUb.call(this,a,false);oUb(this,b)}
function pUb(a){sUb.call(this,'GWT',true);oUb(this,a)}
function bdb(a){this.d=a;this.c=lZ(this.d.b)}
function sTb(a,b){return zTb(a,b,a.b.c)}
function zTb(a,b,c){if(c<0||c>a.b.c){throw new Y9b}a.p&&(b.R[htc]=2,undefined);rTb(a,c,b.R);ahc(a.b,c,b);return b}
function oUb(a,b){a.e=b;!!a.d&&NTb(a.d,a);if(b){b.R.tabIndex=-1;a.R.setAttribute(kuc,zpc)}else{a.R.setAttribute(kuc,$qc)}}
function hZ(a){var b,c;b=BC(a.b._c(cuc),137);if(b==null){c=rC(PT,{123:1,134:1,137:1},1,['New','Open',duc,euc,'Exit']);a.b.bd(cuc,c);return c}else{return b}}
function gZ(a){var b,c;b=BC(a.b._c(buc),137);if(b==null){c=rC(PT,{123:1,134:1,137:1},1,['Undo','Redo','Cut','Copy','Paste']);a.b.bd(buc,c);return c}else{return b}}
function kZ(a){var b,c;b=BC(a.b._c(huc),137);if(b==null){c=rC(PT,{123:1,134:1,137:1},1,['Contents','Fortune Cookie','About GWT']);a.b.bd(huc,c);return c}else{return b}}
function jZ(a){var b,c;b=BC(a.b._c(guc),137);if(b==null){c=rC(PT,{123:1,134:1,137:1},1,['Download','Examples',hpc,"GWT wit' the program"]);a.b.bd(guc,c);return c}else{return b}}
function iZ(a){var b,c;b=BC(a.b._c(fuc),137);if(b==null){c=rC(PT,{123:1,134:1,137:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.bd(fuc,c);return c}else{return b}}
function wUb(){var a;xe(this,$doc.createElement(Qqc));this.R[anc]='gwt-MenuItemSeparator';a=$doc.createElement(fnc);yl(this.R,KWb(a));a[anc]='menuSeparatorInner'}
function lZ(a){var b,c;b=BC(a.b._c(iuc),137);if(b==null){c=rC(PT,{123:1,134:1,137:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.bd(iuc,c);return c}else{return b}}
function Ycb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new bdb(a);n=new OTb;n.c=true;n.R.style[bnc]='500px';n.f=true;q=new PTb(true);p=iZ(a.b);for(k=0;k<p.length;++k){qTb(q,new qUb(p[k],o))}d=new PTb(true);d.f=true;qTb(n,new rUb('File',d));e=hZ(a.b);for(k=0;k<e.length;++k){if(k==3){sTb(d,new wUb);qTb(d,new rUb(e[3],q));sTb(d,new wUb)}else{qTb(d,new qUb(e[k],o))}}b=new PTb(true);qTb(n,new rUb('Edit',b));c=gZ(a.b);for(k=0;k<c.length;++k){qTb(b,new qUb(c[k],o))}f=new PTb(true);qTb(n,new pUb(f));g=jZ(a.b);for(k=0;k<g.length;++k){qTb(f,new qUb(g[k],o))}i=new PTb(true);sTb(n,new wUb);qTb(n,new rUb('Help',i));j=kZ(a.b);for(k=0;k<j.length;++k){qTb(i,new qUb(j[k],o))}$1b(n.R,$mc,juc);MTb(n,juc);return n}
var kuc='aria-haspopup',juc='cwMenuBar',buc='cwMenuBarEditOptions',cuc='cwMenuBarFileOptions',fuc='cwMenuBarFileRecents',guc='cwMenuBarGWTOptions',huc='cwMenuBarHelpOptions',iuc='cwMenuBarPrompts';_=bdb.prototype=adb.prototype=new Y;_.hc=function cdb(){uFb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function ddb(){return wJ};_.cM={81:1};_.b=0;_.d=null;_=edb.prototype;_.fc=function idb(){JX(this.c,Ycb(this.b))};_=OTb.prototype=pTb.prototype;_=rUb.prototype=qUb.prototype=pUb.prototype=kUb.prototype;_=wUb.prototype=vUb.prototype=new se;_.gC=function xUb(){return aP};_.cM={90:1,96:1,109:1};var wJ=Fac(Zrc,'CwMenuBar$1'),aP=Fac(Brc,'MenuItemSeparator');Ymc(Hj)(18);