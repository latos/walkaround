function Scb(){}
function sUb(){}
function LTb(){MTb.call(this,false)}
function nUb(a,b){pUb.call(this,a,false);this.b=b}
function oUb(a,b){pUb.call(this,a,false);lUb(this,b)}
function mUb(a){pUb.call(this,'GWT',true);lUb(this,a)}
function Tcb(a){this.c=a;this.b=bZ(this.c.a)}
function pTb(a,b){return wTb(a,b,a.a.b)}
function wTb(a,b,c){if(c<0||c>a.a.b){throw new U9b}a.o&&(b.Q[Xsc]=2,undefined);oTb(a,c,b.Q);Ygc(a.a,c,b);return b}
function lUb(a,b){a.d=b;!!a.c&&KTb(a.c,a);if(b){b.Q.tabIndex=-1;a.Q.setAttribute($tc,xpc)}else{a.Q.setAttribute($tc,Mqc)}}
function tUb(){var a;ke(this,Kl($doc,Cqc));this.Q[Ymc]='gwt-MenuItemSeparator';a=Kl($doc,cnc);jl(this.Q,HWb(a));a[Ymc]='menuSeparatorInner'}
function ZY(a){var b,c;b=nC(a.a.Xc(Stc),137);if(b==null){c=dC(zT,{123:1,134:1,137:1},1,['New','Open',Ttc,Utc,'Exit']);a.a.Zc(Stc,c);return c}else{return b}}
function YY(a){var b,c;b=nC(a.a.Xc(Rtc),137);if(b==null){c=dC(zT,{123:1,134:1,137:1},1,['Undo','Redo','Cut','Copy','Paste']);a.a.Zc(Rtc,c);return c}else{return b}}
function aZ(a){var b,c;b=nC(a.a.Xc(Xtc),137);if(b==null){c=dC(zT,{123:1,134:1,137:1},1,['Contents','Fortune Cookie','About GWT']);a.a.Zc(Xtc,c);return c}else{return b}}
function _Y(a){var b,c;b=nC(a.a.Xc(Wtc),137);if(b==null){c=dC(zT,{123:1,134:1,137:1},1,['Download','Examples',cpc,"GWT wit' the program"]);a.a.Zc(Wtc,c);return c}else{return b}}
function $Y(a){var b,c;b=nC(a.a.Xc(Vtc),137);if(b==null){c=dC(zT,{123:1,134:1,137:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.a.Zc(Vtc,c);return c}else{return b}}
function bZ(a){var b,c;b=nC(a.a.Xc(Ytc),137);if(b==null){c=dC(zT,{123:1,134:1,137:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.a.Zc(Ytc,c);return c}else{return b}}
function Ocb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Tcb(a);n=new LTb;n.b=true;n.Q.style[Zmc]='500px';n.e=true;q=new MTb(true);p=$Y(a.a);for(k=0;k<p.length;++k){nTb(q,new nUb(p[k],o))}d=new MTb(true);d.e=true;nTb(n,new oUb('File',d));e=ZY(a.a);for(k=0;k<e.length;++k){if(k==3){pTb(d,new tUb);nTb(d,new oUb(e[3],q));pTb(d,new tUb)}else{nTb(d,new nUb(e[k],o))}}b=new MTb(true);nTb(n,new oUb('Edit',b));c=YY(a.a);for(k=0;k<c.length;++k){nTb(b,new nUb(c[k],o))}f=new MTb(true);nTb(n,new mUb(f));g=_Y(a.a);for(k=0;k<g.length;++k){nTb(f,new nUb(g[k],o))}i=new MTb(true);pTb(n,new tUb);nTb(n,new oUb('Help',i));j=aZ(a.a);for(k=0;k<j.length;++k){nTb(i,new nUb(j[k],o))}Z1b(n.Q,Wmc,Ztc);JTb(n,Ztc);return n}
var $tc='aria-haspopup',Ztc='cwMenuBar',Rtc='cwMenuBarEditOptions',Stc='cwMenuBarFileOptions',Vtc='cwMenuBarFileRecents',Wtc='cwMenuBarGWTOptions',Xtc='cwMenuBarHelpOptions',Ytc='cwMenuBarPrompts';_=Tcb.prototype=Scb.prototype=new Y;_.cc=function Ucb(){xFb(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.gC=function Vcb(){return fJ};_.cM={81:1};_.a=0;_.c=null;_=Wcb.prototype;_.ac=function $cb(){zX(this.b,Ocb(this.a))};_=LTb.prototype=mTb.prototype;_=oUb.prototype=nUb.prototype=mUb.prototype=hUb.prototype;_=tUb.prototype=sUb.prototype=new fe;_.gC=function uUb(){return MO};_.cM={90:1,96:1,109:1};var fJ=Bac(Lrc,'CwMenuBar$1'),MO=Bac(nrc,'MenuItemSeparator');Umc(sj)(18);