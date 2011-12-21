function qx(){}
function sx(){sx=imc;rx=new ljc}
function OPb(a,b,c,d){var e;a.a.Hf(b,c);e=QPb(a.a.i,b,c);Ce(e,d,true)}
function tx(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.ad(c)}}
function vx(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Tc(a);return String(c)}
function yx(){sx();var a;a=cC(rx.Xc(jtc),50);if(!a){a=new wx;rx.Zc(jtc,a)}return a}
function ux(c,b){try{typeof $wnd[b]!='object'&&Ax(b);c.a=$wnd[b]}catch(a){Ax(b)}}
function Ax(a){throw new tkc(coc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function wx(){this.b='Dictionary userInfo';ux(this,jtc);if(!this.a){throw new tkc("Cannot find JavaScript object with the name 'userInfo'")}}
function Sab(){var a,b,c,d,e,f,g,i,j,k,n;f=new j2b;g=new MMb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.Q.dir=xnc;g.Q.style['textAlign']=Rnc;g2b(f,new MMb('<b>This example interacts with the following JavaScript variable:<\/b>'));g2b(f,g);j=new FPb;b=j.j;i=yx();e=(n=new ujc,tx(i,n),n);a=0;for(d=agc(Ey(e.a));d.a.gd();){c=cC(hgc(d),1);k=vx(i,c);vPb(j,0,a,c);OPb(b,0,a,'cw-DictionaryExample-header');vPb(j,1,a,k);OPb(b,1,a,'cw-DictionaryExample-data');++a}g2b(f,new MMb('<br><br>'));g2b(f,j);return f}
var jtc='userInfo';_=wx.prototype=qx.prototype=new Y;_.gC=function xx(){return $E};_.Tc=function zx(a){var b;b="Cannot find '"+a+"' in "+this;throw new tkc(b)};_.tS=function Bx(){return this.b};_.cM={50:1};_.a=null;_.b=null;var rx;_=Wab.prototype;_.ac=function $ab(){oX(this.a,Sab())};var $E=hac(grc,Ooc);Amc(tj)(13);