function ay(){}
function cy(){cy=Tmc;by=new Wjc}
function uQb(a,b,c,d){var e;a.b.Ff(b,c);e=wQb(a.b.j,b,c);Pe(e,d,true)}
function dy(d,a){var b=d.b;for(var c in b){b.hasOwnProperty(c)&&a.$c(c)}}
function fy(d,a){a=String(a);var b=d.b;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Rc(a);return String(c)}
function iy(){cy();var a;a=KC(by.Vc(Qtc),51);if(!a){a=new gy;by.Xc(Qtc,a)}return a}
function ey(c,b){try{typeof $wnd[b]!='object'&&ky(b);c.b=$wnd[b]}catch(a){ky(b)}}
function ky(a){throw new clc(Qoc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function gy(){this.c='Dictionary userInfo';ey(this,Qtc);if(!this.b){throw new clc("Cannot find JavaScript object with the name 'userInfo'")}}
function zbb(){var a,b,c,d,e,f,g,i,j,k,n;f=new P2b;g=new sNb('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.R.dir=ioc;g.R.style['textAlign']=Doc;M2b(f,new sNb('<b>\u8FD9\u4E2A\u4F8B\u5B50\u4F7F\u7528\u4E0B\u5217Javascript\u7684\u53D8\u91CF\uFF1A <\/b>'));M2b(f,g);j=new lQb;b=j.k;i=iy();e=(n=new dkc,dy(i,n),n);a=0;for(d=Lgc(oz(e.b));d.b.ed();){c=KC(Sgc(d),1);k=fy(i,c);bQb(j,0,a,c);uQb(b,0,a,'cw-DictionaryExample-header');bQb(j,1,a,k);uQb(b,1,a,'cw-DictionaryExample-data');++a}M2b(f,new sNb('<br><br>'));M2b(f,j);return f}
var Qtc='userInfo';_=gy.prototype=ay.prototype=new Y;_.gC=function hy(){return OF};_.Rc=function jy(a){var b;b="Cannot find '"+a+"' in "+this;throw new clc(b)};_.tS=function ly(){return this.c};_.cM={51:1};_.b=null;_.c=null;var by;_=Dbb.prototype;_.fc=function Hbb(){XX(this.b,zbb())};var OF=Sac(Xrc,'Dictionary');jnc(Hj)(13);