function Qz(){}
function Sz(){Sz=hRc;Rz=new kOc}
function Lic(a,b,c,d){var e;a.a.Eg(b,c);e=Nic(a.a.i,b,c);Be(e,d,true)}
function Tz(d,a){var b=d.a;for(var c in b){b.hasOwnProperty(c)&&a.$d(c)}}
function Vz(d,a){a=String(a);var b=d.a;var c=b[a];(c==null||!b.hasOwnProperty(a))&&d.Cd(a);return String(c)}
function Yz(){Sz();var a;a=I3(Rz.Vd(qYc),51);if(!a){a=new Wz;Rz.Xd(qYc,a)}return a}
function Uz(c,b){try{typeof $wnd[b]!='object'&&$z(b);c.a=$wnd[b]}catch(a){$z(b)}}
function $z(a){throw new sPc(dTc+a+"' is not a JavaScript object and cannot be used as a Dictionary")}
function Wz(){this.b='Dictionary userInfo';Uz(this,qYc);if(!this.a){throw new sPc("Cannot find JavaScript object with the name 'userInfo'")}}
function IFb(){var a,b,c,d,e,f,g,i,j,k,n;f=new gxc;g=new Jfc('<pre>var userInfo = {\n&nbsp;&nbsp;name: "Amelie Crutcher",\n&nbsp;&nbsp;timeZone: "EST",\n&nbsp;&nbsp;userID: "123",\n&nbsp;&nbsp;lastLogOn: "2/2/2006"\n};<\/pre>\n');g.Q.dir=ySc;g.Q.style['textAlign']=TSc;dxc(f,new Jfc('<b>\u0647\u0630\u0627 \u0627\u0644\u0645\u062B\u0627\u0644 \u064A\u062A\u0641\u0627\u0639\u0644 \u0645\u0639 \u0645\u062A\u063A\u064A\u0631\u0627\u062A \u062C\u0627\u0641\u0627\u0633\u0643\u0631\u064A\u0628\u062A \u0627\u0644\u062A\u0627\u0644\u064A\u0629 :<\/b>'));dxc(f,g);j=new Cic;b=j.j;i=Yz();e=(n=new tOc,Tz(i,n),n);a=0;for(d=_Kc(MG(e.a));d.a.ee();){c=I3(gLc(d),1);k=Vz(i,c);sic(j,0,a,c);Lic(b,0,a,'cw-DictionaryExample-header');sic(j,1,a,k);Lic(b,1,a,'cw-DictionaryExample-data');++a}dxc(f,new Jfc('<br><br>'));dxc(f,j);return f}
var qYc='userInfo';_=Wz.prototype=Qz.prototype=new Y;_.gC=function Xz(){return Y6};_.Cd=function Zz(a){var b;b="Cannot find '"+a+"' in "+this;throw new sPc(b)};_.tS=function _z(){return this.b};_.cM={51:1};_.a=null;_.b=null;var Rz;_=MFb.prototype;_.ac=function QFb(){eqb(this.a,IFb())};var Y6=gFc(qWc,'Dictionary');zRc(sj)(13);