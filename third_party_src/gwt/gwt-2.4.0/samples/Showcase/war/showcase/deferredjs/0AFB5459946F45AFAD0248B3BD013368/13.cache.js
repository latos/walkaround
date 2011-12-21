function mx(){}
function ox(){ox=Nlc;nx=new Qic}
function px(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.ad(c)}}
function APb(a,b,c,d){var e;a.b.Hf(b,c);e=CPb(a.b.j,b,c);Ce(e,d,true)}
function ux(){ox();var a;a=$B(nx.Xc(Qsc),50);if(!a){a=new sx;nx.Zc(Qsc,a)}return a}
function qx(c,b){try{typeof $wnd[b]!='object'&&wx(b);c.b=$wnd[b]}catch(a){wx(b)}}
function wx(a){throw new Yjc(Knc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function rx(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Tc(a);return String(c)}
function sx(){this.c='Dictionary userInfo';qx(this,Qsc);if(!this.b){throw new Yjc("Cannot find JavaScript object with the name 'userInfo'")}}
function Hab(){var a,b,c,d,e,f,g,i,j,k,n;f=new V1b;g=new sMb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=dnc;g.R.style['textAlign']=xnc;S1b(f,new sMb('<b>This example interacts with the following JavaScript variable:<\/b>'));S1b(f,g);j=new rPb;b=j.k;i=ux();e=(n=new Zic,px(i,n),n);a=0;for(d=Ffc(Ay(e.b));d.b.gd();){c=$B(Mfc(d),1);k=rx(i,c);hPb(j,0,a,c);APb(b,0,a,'cw-DictionaryExample-header');hPb(j,1,a,k);APb(b,1,a,'cw-DictionaryExample-data');++a}S1b(f,new sMb('<br><br>'));S1b(f,j);return f}
var Qsc='userInfo';_=sx.prototype=mx.prototype=new Y;_.gC=function tx(){return YE};_.Tc=function vx(a){var b;b="Cannot find '"+a+"' in "+this;throw new Yjc(b)};_.tS=function xx(){return this.c};_.cM={50:1};_.b=null;_.c=null;var nx;_=Lab.prototype;_.bc=function Pab(){dX(this.b,Hab())};var YE=M9b(Pqc,uoc);dmc(tj)(13);