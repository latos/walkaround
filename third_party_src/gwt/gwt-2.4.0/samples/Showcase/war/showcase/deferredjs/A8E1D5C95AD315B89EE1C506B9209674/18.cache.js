function Hcb(){}
function aUb(){}
function tTb(){uTb.call(this,false)}
function XTb(a,b){ZTb.call(this,a,false);this.b=b}
function YTb(a,b){ZTb.call(this,a,false);VTb(this,b)}
function WTb(a){ZTb.call(this,'GWT',true);VTb(this,a)}
function Icb(a){this.c=a;this.b=SY(this.c.a)}
function ZSb(a,b){return eTb(a,b,a.a.b)}
function eTb(a,b,c){if(c<0||c>a.a.b){throw new A9b}a.o&&(b.Q[Esc]=2,undefined);YSb(a,c,b.Q);Egc(a.a,c,b);return b}
function VTb(a,b){a.d=b;!!a.c&&sTb(a.c,a);if(b){b.Q.tabIndex=-1;a.Q.setAttribute(Htc,_oc)}else{a.Q.setAttribute(Htc,tqc)}}
function OY(a){var b,c;b=cC(a.a.Xc(ztc),137);if(b==null){c=UB(oT,{123:1,134:1,137:1},1,['New','Open',Atc,Btc,'Exit']);a.a.Zc(ztc,c);return c}else{return b}}
function NY(a){var b,c;b=cC(a.a.Xc(ytc),137);if(b==null){c=UB(oT,{123:1,134:1,137:1},1,['Undo','Redo','Cut','Copy','Paste']);a.a.Zc(ytc,c);return c}else{return b}}
function RY(a){var b,c;b=cC(a.a.Xc(Etc),137);if(b==null){c=UB(oT,{123:1,134:1,137:1},1,['Contents','Fortune Cookie','About GWT']);a.a.Zc(Etc,c);return c}else{return b}}
function QY(a){var b,c;b=cC(a.a.Xc(Dtc),137);if(b==null){c=UB(oT,{123:1,134:1,137:1},1,['Download','Examples',Joc,"GWT wit' the program"]);a.a.Zc(Dtc,c);return c}else{return b}}
function PY(a){var b,c;b=cC(a.a.Xc(Ctc),137);if(b==null){c=UB(oT,{123:1,134:1,137:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.a.Zc(Ctc,c);return c}else{return b}}
function bUb(){var a;ke(this,$doc.createElement(jqc));this.Q[Emc]='gwt-MenuItemSeparator';a=$doc.createElement(Jmc);kl(this.Q,pWb(a));a[Emc]='menuSeparatorInner'}
function SY(a){var b,c;b=cC(a.a.Xc(Ftc),137);if(b==null){c=UB(oT,{123:1,134:1,137:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.a.Zc(Ftc,c);return c}else{return b}}
function Dcb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Icb(a);n=new tTb;n.b=true;n.Q.style[Fmc]='500px';n.e=true;q=new uTb(true);p=PY(a.a);for(k=0;k<p.length;++k){XSb(q,new XTb(p[k],o))}d=new uTb(true);d.e=true;XSb(n,new YTb('File',d));e=OY(a.a);for(k=0;k<e.length;++k){if(k==3){ZSb(d,new bUb);XSb(d,new YTb(e[3],q));ZSb(d,new bUb)}else{XSb(d,new XTb(e[k],o))}}b=new uTb(true);XSb(n,new YTb('Edit',b));c=NY(a.a);for(k=0;k<c.length;++k){XSb(b,new XTb(c[k],o))}f=new uTb(true);XSb(n,new WTb(f));g=QY(a.a);for(k=0;k<g.length;++k){XSb(f,new XTb(g[k],o))}i=new uTb(true);ZSb(n,new bUb);XSb(n,new YTb('Help',i));j=RY(a.a);for(k=0;k<j.length;++k){XSb(i,new XTb(j[k],o))}H1b(n.Q,Cmc,Gtc);rTb(n,Gtc);return n}
var Htc='aria-haspopup',Gtc='cwMenuBar',ytc='cwMenuBarEditOptions',ztc='cwMenuBarFileOptions',Ctc='cwMenuBarFileRecents',Dtc='cwMenuBarGWTOptions',Etc='cwMenuBarHelpOptions',Ftc='cwMenuBarPrompts';_=Icb.prototype=Hcb.prototype=new Y;_.cc=function Jcb(){$Eb(this.b[this.a]);this.a=(this.a+1)%this.b.length};_.gC=function Kcb(){return WI};_.cM={81:1};_.a=0;_.c=null;_=Lcb.prototype;_.ac=function Pcb(){oX(this.b,Dcb(this.a))};_=tTb.prototype=WSb.prototype;_=YTb.prototype=XTb.prototype=WTb.prototype=RTb.prototype;_=bUb.prototype=aUb.prototype=new fe;_.gC=function cUb(){return BO};_.cM={90:1,96:1,109:1};var WI=hac(src,'CwMenuBar$1'),BO=hac(Wqc,'MenuItemSeparator');Amc(tj)(18);