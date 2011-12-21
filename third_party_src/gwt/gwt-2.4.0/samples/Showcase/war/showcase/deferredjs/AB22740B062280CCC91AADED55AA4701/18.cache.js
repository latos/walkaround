function Bdb(){}
function WUb(){}
function nUb(){oUb.call(this,false)}
function RUb(a,b){TUb.call(this,a,false);this.c=b}
function SUb(a,b){TUb.call(this,a,false);PUb(this,b)}
function QUb(a){TUb.call(this,'GWT',true);PUb(this,a)}
function Cdb(a){this.d=a;this.c=NZ(this.d.b)}
function STb(a,b){return $Tb(a,b,a.b.c)}
function $Tb(a,b,c){if(c<0||c>a.b.c){throw new Dac}a.p&&(b.R[Htc]=2,undefined);RTb(a,c,b.R);Hhc(a.b,c,b);return b}
function PUb(a,b){a.e=b;!!a.d&&mUb(a.d,a);if(b){(WQb(),b.R).tabIndex=-1;a.R.setAttribute(Cuc,hqc)}else{a.R.setAttribute(Cuc,Brc)}}
function XUb(){var a;ye(this,$doc.createElement(rrc));this.R[Hnc]='gwt-MenuItemSeparator';a=$doc.createElement(Mnc);Sl(this.R,jXb(a));a[Hnc]='menuSeparatorInner'}
function LZ(a){var b,c;b=TC(a.b.Zc(yuc),138);if(b==null){c=JC(pU,{124:1,135:1,138:1},1,['\u4E0B\u8F7D',Spc,Upc,'GWT \u9AD8\u624B\u7A0B\u5E8F']);a.b._c(yuc,c);return c}else{return b}}
function MZ(a){var b,c;b=TC(a.b.Zc(zuc),138);if(b==null){c=JC(pU,{124:1,135:1,138:1},1,['\u5185\u5BB9','\u5E78\u8FD0\u997C','\u5173\u4E8EGWT']);a.b._c(zuc,c);return c}else{return b}}
function IZ(a){var b,c;b=TC(a.b.Zc(uuc),138);if(b==null){c=JC(pU,{124:1,135:1,138:1},1,['\u64A4\u6D88','\u91CD\u590D','\u526A\u5207','\u590D\u5236','\u7C98\u8D34']);a.b._c(uuc,c);return c}else{return b}}
function JZ(a){var b,c;b=TC(a.b.Zc(vuc),138);if(b==null){c=JC(pU,{124:1,135:1,138:1},1,['\u65B0\u5EFA','\u6253\u5F00',wuc,'\u8FD1\u671F\u6587\u4EF6','\u9000\u51FA']);a.b._c(vuc,c);return c}else{return b}}
function KZ(a){var b,c;b=TC(a.b.Zc(xuc),138);if(b==null){c=JC(pU,{124:1,135:1,138:1},1,['Fishing in the desert.txt','How to tame a wild parrot','Idiots Guide to Emu Farms']);a.b._c(xuc,c);return c}else{return b}}
function NZ(a){var b,c;b=TC(a.b.Zc(Auc),138);if(b==null){c=JC(pU,{124:1,135:1,138:1},1,['\u611F\u8C22\u60A8\u9009\u62E9\u83DC\u5355\u9879','\u9009\u5F97\u5F88\u4E0D\u9519','\u9664\u4E86\u9009\u62E9\u83DC\u5355\u9879\u4E4B\u5916\u96BE\u9053\u6CA1\u6709\u66F4\u597D\u7684\u9009\u62E9\uFF1F','\u8BD5\u8BD5\u522B\u7684\u5427','\u8FD9\u4E0D\u8FC7\u662F\u4E2A\u83DC\u5355\u800C\u5DF2\uFF01','\u53C8\u6D6A\u8D39\u4E86\u4E00\u6B21\u70B9\u51FB']);a.b._c(Auc,c);return c}else{return b}}
function xdb(a){var b,c,d,e,f,g,i,j,k,n,o,p,q;o=new Cdb(a);n=new nUb;n.c=true;n.R.style[Inc]='500px';n.f=true;q=new oUb(true);p=KZ(a.b);for(k=0;k<p.length;++k){QTb(q,new RUb(p[k],o))}d=new oUb(true);d.f=true;QTb(n,new SUb('\u6587\u4EF6',d));e=JZ(a.b);for(k=0;k<e.length;++k){if(k==3){STb(d,new XUb);QTb(d,new SUb(e[3],q));STb(d,new XUb)}else{QTb(d,new RUb(e[k],o))}}b=new oUb(true);QTb(n,new SUb('\u7F16\u8F91',b));c=IZ(a.b);for(k=0;k<c.length;++k){QTb(b,new RUb(c[k],o))}f=new oUb(true);QTb(n,new QUb(f));g=LZ(a.b);for(k=0;k<g.length;++k){QTb(f,new RUb(g[k],o))}i=new oUb(true);STb(n,new XUb);QTb(n,new SUb('\u5E2E\u52A9',i));j=MZ(a.b);for(k=0;k<j.length;++k){QTb(i,new RUb(j[k],o))}z2b(n.R,Fnc,Buc);lUb(n,Buc);return n}
var Cuc='aria-haspopup',Buc='cwMenuBar',uuc='cwMenuBarEditOptions',vuc='cwMenuBarFileOptions',xuc='cwMenuBarFileRecents',yuc='cwMenuBarGWTOptions',zuc='cwMenuBarHelpOptions',Auc='cwMenuBarPrompts';_=Cdb.prototype=Bdb.prototype=new Y;_.hc=function Ddb(){TFb(this.c[this.b]);this.b=(this.b+1)%this.c.length};_.gC=function Edb(){return WJ};_.cM={82:1};_.b=0;_.d=null;_=Fdb.prototype;_.fc=function Jdb(){jY(this.c,xdb(this.b))};_=nUb.prototype=PTb.prototype;_=SUb.prototype=RUb.prototype=QUb.prototype=LUb.prototype;_=XUb.prototype=WUb.prototype=new te;_.gC=function YUb(){return AP};_.cM={91:1,97:1,110:1};var WJ=kbc(zsc,'CwMenuBar$1'),AP=kbc(bsc,'MenuItemSeparator');Dnc(Hj)(18);