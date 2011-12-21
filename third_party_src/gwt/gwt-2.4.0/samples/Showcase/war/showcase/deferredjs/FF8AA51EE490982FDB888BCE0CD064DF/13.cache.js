function Dx(){}
function Fx(){Fx=vmc;Ex=new yjc}
function Gx(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.Wc(c)}}
function _Pb(a,b,c,d){var e;a.a.Bf(b,c);e=bQb(a.a.i,b,c);Ce(e,d,true)}
function Lx(){Fx();var a;a=lC(Ex.Rc(ltc),51);if(!a){a=new Jx;Ex.Tc(ltc,a)}return a}
function Hx(c,b){try{typeof $wnd[b]!='object'&&Nx(b);c.a=$wnd[b]}catch(a){Nx(b)}}
function Nx(a){throw new Gkc(qoc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Ix(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Nc(a);return String(c)}
function Jx(){this.b='Dictionary userInfo';Hx(this,ltc);if(!this.a){throw new Gkc("Cannot find JavaScript object with the name 'userInfo'")}}
function ebb(){var a,b,c,d,e,f,g,i,j,k,n;f=new w2b;g=new ZMb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.Q.dir=Knc;g.Q.style['textAlign']=doc;t2b(f,new ZMb('<b>\u8FD9\u4E2A\u4F8B\u5B50\u4F7F\u7528\u4E0B\u5217Javascript\u7684\u53D8\u91CF\uFF1A <\/b>'));t2b(f,g);j=new SPb;b=j.j;i=Lx();e=(n=new Hjc,Gx(i,n),n);a=0;for(d=ngc(Ry(e.a));d.a.ad();){c=lC(ugc(d),1);k=Ix(i,c);IPb(j,0,a,c);_Pb(b,0,a,'cw-DictionaryExample-header');IPb(j,1,a,k);_Pb(b,1,a,'cw-DictionaryExample-data');++a}t2b(f,new ZMb('<br><br>'));t2b(f,j);return f}
var ltc='userInfo';_=Jx.prototype=Dx.prototype=new Y;_.gC=function Kx(){return lF};_.Nc=function Mx(a){var b;b="Cannot find '"+a+"' in "+this;throw new Gkc(b)};_.tS=function Ox(){return this.b};_.cM={51:1};_.a=null;_.b=null;var Ex;_=ibb.prototype;_.ac=function mbb(){CX(this.a,ebb())};var lF=uac(qrc,'Dictionary');Nmc(tj)(13);