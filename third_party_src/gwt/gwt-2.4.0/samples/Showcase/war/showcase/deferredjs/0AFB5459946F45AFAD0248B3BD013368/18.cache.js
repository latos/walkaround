function wcb(){}
function OTb(){}
function fTb(){gTb.call(this,false)}
function JTb(a,b){LTb.call(this,a,false);this.c=b}
function KTb(a,b){LTb.call(this,a,false);HTb(this,b)}
function ITb(a){LTb.call(this,'GWT',true);HTb(this,a)}
function xcb(a){this.d=a;this.c=HY(this.d.b)}
function LSb(a,b){return SSb(a,b,a.b.c)}
function SSb(a,b,c){if(c<0||c>a.b.c){throw new d9b}a.p&&(b.R[jsc]=2,undefined);KSb(a,c,b.R);hgc(a.b,c,b);return b}
function HTb(a,b){a.e=b;!!a.d&&eTb(a.d,a);if(b){b.R.tabIndex=-1;a.R.setAttribute(mtc,Hoc)}else{a.R.setAttribute(mtc,aqc)}}
function DY(a){var b,c;b=$B(a.b.Xc(etc),137);if(b==null){c=QB(jT,{123:1,134:1,137:1},1,['New','Open',ftc,gtc,'Exit']);a.b.Zc(etc,c);return c}else{return b}}
function CY(a){var b,c;b=$B(a.b.Xc(dtc),137);if(b==null){c=QB(jT,{123:1,134:1,137:1},1,['Undo','Redo','Cut','Copy','Paste']);a.b.Zc(dtc,c);return c}else{return b}}
function GY(a){var b,c;b=$B(a.b.Xc(jtc),137);if(b==null){c=QB(jT,{123:1,134:1,137:1},1,['Contents','Fortune Cookie','About GWT']);a.b.Zc(jtc,c);return c}else{return b}}
function FY(a){var b,c;b=$B(a.b.Xc(itc),137);if(b==null){c=QB(jT,{123:1,134:1,137:1},1,['Download','Examples',poc,"GWT wit' the program"]);a.b.Zc(itc,c);return c}else{return b}}
function EY(a){var b,c;b=$B(a.b.Xc(htc),137);if(b==null){c=QB(jT,{123:1,134:1,137:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b.Zc(htc,c);return c}else{return b}}
function PTb(){var a;ke(this,$doc.createElement(Spc));this.R[hmc]='gwt-MenuItemSeparator';a=$doc.createElement(mmc);kl(this.R,bWb(a));a[hmc]='menuSeparatorInner'}
function HY(a){var b,c;b=$B(a.b.Xc(ktc),137);if(b==null){c=QB(jT,{123:1,134:1,137:1},1,['Thank you for selecting a menu item','A fine selection indeed',"Don't you have anything better to do than select menu items?",'Try something else','this is just a menu!','Another wasted click']);a.b.Zc(ktc,c);return c}else{return b}}
function scb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new xcb(a);n=new fTb;n.c=true;n.R.style[imc]='500px';n.f=true;q=new gTb(true);p=EY(a.b);for(k=0;k<p.length;++k){JSb(q,new JTb(p[k],o))}d=new gTb(true);d.f=true;JSb(n,new KTb('File',d));e=DY(a.b);for(k=0;k<e.length;++k){if(k==3){LSb(d,new PTb);JSb(d,new KTb(e[3],q));LSb(d,new PTb)}else{JSb(d,new JTb(e[k],o))}}b=new gTb(true);JSb(n,new KTb('Edit',b));c=CY(a.b);for(k=0;k<c.length;++k){JSb(b,new JTb(c[k],o))}f=new gTb(true);JSb(n,new ITb(f));g=FY(a.b);for(k=0;k<g.length;++k){JSb(f,new JTb(g[k],o))}i=new gTb(true);LSb(n,new PTb);JSb(n,new KTb('Help',i));j=GY(a.b);for(k=0;k<j.length;++k){JSb(i,new JTb(j[k],o))}r1b(n.R,fmc,ltc);dTb(n,ltc);return n}
var mtc='aria-haspopup',ltc='cwMenuBar',dtc='cwMenuBarEditOptions',etc='cwMenuBarFileOptions',htc='cwMenuBarFileRecents',itc='cwMenuBarGWTOptions',jtc='cwMenuBarHelpOptions',ktc='cwMenuBarPrompts';_=xcb.prototype=wcb.prototype=new Y;_.dc=function ycb(){NEb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function zcb(){return TI};_.cM={81:1};_.b=0;_.d=null;_=Acb.prototype;_.bc=function Ecb(){dX(this.c,scb(this.b))};_=fTb.prototype=ISb.prototype;_=KTb.prototype=JTb.prototype=ITb.prototype=DTb.prototype;_=PTb.prototype=OTb.prototype=new fe;_.gC=function QTb(){return xO};_.cM={90:1,96:1,109:1};var TI=M9b(_qc,'CwMenuBar$1'),xO=M9b(Dqc,'MenuItemSeparator');dmc(tj)(18);