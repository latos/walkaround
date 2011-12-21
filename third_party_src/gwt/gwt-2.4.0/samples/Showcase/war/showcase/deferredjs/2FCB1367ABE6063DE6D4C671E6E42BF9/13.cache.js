function Px(){}
function Rx(){Rx=Gmc;Qx=new Jjc}
function hQb(a,b,c,d){var e;a.b.Lf(b,c);e=jQb(a.b.j,b,c);Pe(e,d,true)}
function Sx(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ed(c)}}
function Ux(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Xc(a);return String(c)}
function Xx(){Rx();var a;a=BC(Qx._c(Otc),50);if(!a){a=new Vx;Qx.bd(Otc,a)}return a}
function Tx(c,b){try{typeof $wnd[b]!='object'&&Zx(b);c.b=$wnd[b]}catch(a){Zx(b)}}
function Zx(a){throw new Rkc(Coc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Vx(){this.c='Dictionary userInfo';Tx(this,Otc);if(!this.b){throw new Rkc("Cannot find JavaScript object with the name 'userInfo'")}}
function lbb(){var a,b,c,d,e,f,g,i,j,k,n;f=new C2b;g=new fNb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=Xnc;g.R.style['textAlign']=poc;z2b(f,new fNb('<b>This example interacts with the following JavaScript variable:<\/b>'));z2b(f,g);j=new $Pb;b=j.k;i=Xx();e=(n=new Sjc,Sx(i,n),n);a=0;for(d=ygc(bz(e.b));d.b.ld();){c=BC(Fgc(d),1);k=Ux(i,c);QPb(j,0,a,c);hQb(b,0,a,'cw-DictionaryExample-header');QPb(j,1,a,k);hQb(b,1,a,'cw-DictionaryExample-data');++a}z2b(f,new fNb('<br><br>'));z2b(f,j);return f}
var Otc='userInfo';_=Vx.prototype=Px.prototype=new Y;_.gC=function Wx(){return BF};_.Xc=function Yx(a){var b;b="Cannot find '"+a+"' in "+this;throw new Rkc(b)};_.tS=function $x(){return this.c};_.cM={50:1};_.b=null;_.c=null;var Qx;_=pbb.prototype;_.fc=function tbb(){JX(this.b,lbb())};var BF=Fac(Nrc,mpc);Ymc(Hj)(13);