function Ox(){}
function Qx(){Qx=Pmc;Px=new Sjc}
function rQb(a,b,c,d){var e;a.a.Bf(b,c);e=tQb(a.a.i,b,c);Be(e,d,true)}
function Rx(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.Wc(c)}}
function Tx(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Nc(a);return String(c)}
function Wx(){Qx();var a;a=wC(Px.Rc(Ftc),51);if(!a){a=new Ux;Px.Tc(Ftc,a)}return a}
function Sx(c,b){try{typeof $wnd[b]!='object'&&Yx(b);c.a=$wnd[b]}catch(a){Yx(b)}}
function Yx(a){throw new $kc(Loc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Ux(){this.b='Dictionary userInfo';Sx(this,Ftc);if(!this.a){throw new $kc("Cannot find JavaScript object with the name 'userInfo'")}}
function pbb(){var a,b,c,d,e,f,g,i,j,k,n;f=new O2b;g=new pNb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.Q.dir=eoc;g.Q.style['textAlign']=zoc;L2b(f,new pNb('<b>\u8FD9\u4E2A\u4F8B\u5B50\u4F7F\u7528\u4E0B\u5217Javascript\u7684\u53D8\u91CF\uFF1A <\/b>'));L2b(f,g);j=new iQb;b=j.j;i=Wx();e=(n=new _jc,Rx(i,n),n);a=0;for(d=Hgc(az(e.a));d.a.ad();){c=wC(Ogc(d),1);k=Tx(i,c);$Pb(j,0,a,c);rQb(b,0,a,'cw-DictionaryExample-header');$Pb(j,1,a,k);rQb(b,1,a,'cw-DictionaryExample-data');++a}L2b(f,new pNb('<br><br>'));L2b(f,j);return f}
var Ftc='userInfo';_=Ux.prototype=Ox.prototype=new Y;_.gC=function Vx(){return wF};_.Nc=function Xx(a){var b;b="Cannot find '"+a+"' in "+this;throw new $kc(b)};_.tS=function Zx(){return this.b};_.cM={51:1};_.a=null;_.b=null;var Px;_=tbb.prototype;_.ac=function xbb(){NX(this.a,pbb())};var wF=Oac(Krc,'Dictionary');fnc(sj)(13);