function zx(){}
function Bx(){Bx=$lc;Ax=new bjc}
function NPb(a,b,c,d){var e;a.b.Bf(b,c);e=PPb(a.b.j,b,c);Ce(e,d,true)}
function Cx(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.Wc(c)}}
function Ex(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Nc(a);return String(c)}
function Hx(){Bx();var a;a=hC(Ax.Rc(Ssc),51);if(!a){a=new Fx;Ax.Tc(Ssc,a)}return a}
function Dx(c,b){try{typeof $wnd[b]!='object'&&Jx(b);c.b=$wnd[b]}catch(a){Jx(b)}}
function Jx(a){throw new jkc(Ync+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Fx(){this.c='Dictionary userInfo';Dx(this,Ssc);if(!this.b){throw new jkc("Cannot find JavaScript object with the name 'userInfo'")}}
function Vab(){var a,b,c,d,e,f,g,i,j,k,n;f=new g2b;g=new FMb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=qnc;g.R.style['textAlign']=Lnc;d2b(f,new FMb('<b>\u8FD9\u4E2A\u4F8B\u5B50\u4F7F\u7528\u4E0B\u5217Javascript\u7684\u53D8\u91CF\uFF1A <\/b>'));d2b(f,g);j=new EPb;b=j.k;i=Hx();e=(n=new kjc,Cx(i,n),n);a=0;for(d=Sfc(Ny(e.b));d.b.ad();){c=hC(Zfc(d),1);k=Ex(i,c);uPb(j,0,a,c);NPb(b,0,a,'cw-DictionaryExample-header');uPb(j,1,a,k);NPb(b,1,a,'cw-DictionaryExample-data');++a}d2b(f,new FMb('<br><br>'));d2b(f,j);return f}
var Ssc='userInfo';_=Fx.prototype=zx.prototype=new Y;_.gC=function Gx(){return jF};_.Nc=function Ix(a){var b;b="Cannot find '"+a+"' in "+this;throw new jkc(b)};_.tS=function Kx(){return this.c};_.cM={51:1};_.b=null;_.c=null;var Ax;_=Zab.prototype;_.bc=function bbb(){rX(this.b,Vab())};var jF=Z9b(Zqc,'Dictionary');qmc(tj)(13);