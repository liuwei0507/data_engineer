# Vagrant 连接virtual box
```shell
Vagrant.configure("2") do |config|
  # 定义虚拟机数量和基本配置
  (1..3).each do |i|
    config.vm.define "linux12#{i}" do |web|
      web.vm.box = "centos/stream9"  # 使用 centos

      # 设置网络
      web.vm.network "private_network", ip: "192.168.33.#{i + 10}"
      # 设置端口转发
#       web.vm.network "forwarded_port", guest: 8032, host: 8032, host_ip: "0.0.0.0", auto_correct: true # resource manager 端口
      
      # 设置主机名
      web.vm.hostname = "linux12#{i}"

      # 可选：设置虚拟机内存和 CPU
      web.vm.provider "virtualbox" do |vb|
        vb.memory = "1024"  # 设置内存
        vb.cpus = 1         # 设置 CPU 数量
      end


      web.vm.provision "shell", inline: <<-SHELL
          # 设置root用户密码
          echo "root:vagrant" | chpasswd
          sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
          systemctl restart sshd

          # 配置使用host访问其他虚拟机
          echo "192.168.33.11 linux121" >> /etc/hosts
          echo "192.168.33.12 linux122" >> /etc/hosts
          echo "192.168.33.13 linux123" >> /etc/hosts
       SHELL

      # 可选：设置同步文件夹
#     web.vm.synced_folder ".", "/vagrant_data"  # 将当前目录同步到虚拟机的 /vagrant_data

      # Provisioning: 安装 JDK 8 和 Docker --- 手动安装
#       web.vm.provision "shell", inline: <<-SHELL
#           # 更新系统
#           yum update -y
#
#           # 安装 Java JDK 8
#           yum install -y java-1.8.0-openjdk-devel
#
#           # 安装 Docker
#           yum install -y yum-utils
#           yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
#           yum install -y docker-ce docker-ce-cli containerd.io
#
#           # 启动 Docker 服务
#           systemctl start docker
#           systemctl enable docker
#
#           # 验证安装
#           java -version
#           docker --version
#         SHELL
    end
  end
end
```

# Vagrant 连接docker
```shell
Vagrant.configure("2") do |config|
  (1..3).each do |i|
    config.vm.define "centos-vm#{i}" do |vm|
      vm.vm.provider "docker" do |d|
        d.image = "centos:7"
        d.name = "centos-vm#{i}"
        d.cmd = ["tail", "-f", "/dev/null"] # 保持容器运行
        d.privileged = true # 允许特权模式
      end
      # 允许使用 SSH 访问
      vm.vm.network "private_network", type: "dhcp"
    end
  end
end
```
## 遇到问题
### 容器创建就关闭
```shell
d.cmd = ["tail", "-f", "/dev/null"] # 保持容器运行
```

### 不能使用vagrant ssh进入虚拟机容器
```shell
# 使用docker exec 进入
vagrant docker-exec -it centos-vm3 -- bash
```

### 复制文件到虚拟机
```shell
# 使用docker cp拷贝文件
docker cp Vagrantfile centos-vm1:/home/hadoop/data
```

