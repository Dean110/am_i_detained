# AWS Elastic Beanstalk Deployment Guide

## Prerequisites

### 1. AWS Account Setup
- Create an AWS account if you don't have one
- Note your AWS region (e.g., `us-east-1`)

### 2. Create IAM User for GitHub Actions
1. Go to AWS Console → IAM → Users → Create User
2. User name: `github-actions-deployer`
3. Attach policies:
   - `AdministratorAccess-AWSElasticBeanstalk`
   - `AmazonS3FullAccess` (for deployment artifacts)
4. Create access key → Save **Access Key ID** and **Secret Access Key**

### 3. Create Elastic Beanstalk Application

#### Option A: Using AWS Console
1. Go to Elastic Beanstalk console
2. Create Application:
   - **Application name**: `am-i-detained`
   - **Platform**: Java
   - **Platform branch**: Corretto 21 (or latest)
   - **Environment name**: `am-i-detained-prod`
   - **Domain**: (auto-generated or custom)

#### Option B: Using AWS CLI
```bash
# Install AWS CLI if needed
brew install awscli  # macOS
# or: pip install awscli

# Configure AWS CLI
aws configure

# Create application
aws elasticbeanstalk create-application \
  --application-name am-i-detained \
  --description "Check-in alert system" \
  --region us-east-1

# Create environment
aws elasticbeanstalk create-environment \
  --application-name am-i-detained \
  --environment-name am-i-detained-prod \
  --solution-stack-name "64bit Amazon Linux 2023 v4.3.0 running Corretto 21" \
  --option-settings \
    Namespace=aws:autoscaling:launchconfiguration,OptionName=InstanceType,Value=t3.micro \
    Namespace=aws:elasticbeanstalk:environment,OptionName=EnvironmentType,Value=SingleInstance \
  --region us-east-1
```

### 4. Create RDS PostgreSQL Database (Optional but Recommended)

#### Option A: Through Elastic Beanstalk Console
1. Go to your environment → Configuration → Database
2. Click "Edit"
3. Settings:
   - **Engine**: postgres
   - **Engine version**: 15.x
   - **Instance class**: db.t3.micro
   - **Storage**: 20 GB
   - **Username**: postgres
   - **Password**: (create strong password)
4. Apply changes (takes ~10 minutes)

#### Option B: Separate RDS Instance (Better for Production)
```bash
aws rds create-db-instance \
  --db-instance-identifier am-i-detained-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --master-username postgres \
  --master-user-password YOUR_STRONG_PASSWORD \
  --allocated-storage 20 \
  --vpc-security-group-ids sg-xxxxx \
  --db-name am_i_detained \
  --region us-east-1
```

### 5. Configure GitHub Secrets
1. Go to your GitHub repository
2. Settings → Secrets and variables → Actions → New repository secret
3. Add these secrets:
   - `AWS_ACCESS_KEY_ID`: (from step 2)
   - `AWS_SECRET_ACCESS_KEY`: (from step 2)

### 6. Update Elastic Beanstalk Configuration

Update the `.ebextensions` files with your actual values:

#### `.ebextensions/02-oauth-secrets.config`
```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    GOOGLE_CLIENT_ID: 'your-actual-google-client-id'
    GOOGLE_CLIENT_SECRET: 'your-actual-google-secret'
    FACEBOOK_CLIENT_ID: 'your-actual-facebook-id'
    FACEBOOK_CLIENT_SECRET: 'your-actual-facebook-secret'
    INSTAGRAM_CLIENT_ID: 'your-actual-instagram-id'
    INSTAGRAM_CLIENT_SECRET: 'your-actual-instagram-secret'
```

#### `.ebextensions/03-database.config`
```yaml
option_settings:
  aws:elasticbeanstalk:application:environment:
    DATABASE_URL: 'jdbc:postgresql://your-rds-endpoint.rds.amazonaws.com:5432/am_i_detained'
    DATABASE_USERNAME: 'postgres'
    DATABASE_PASSWORD: 'your-actual-password'
```

**Note**: You can also set these via AWS Console:
- Go to Environment → Configuration → Software → Environment properties

### 7. Update OAuth Redirect URIs

Update your OAuth app settings with the Elastic Beanstalk URL:

**Google Console:**
- Authorized redirect URIs: `http://your-app.elasticbeanstalk.com/login/oauth2/code/google`

**Facebook Console:**
- Valid OAuth Redirect URIs: `http://your-app.elasticbeanstalk.com/login/oauth2/code/facebook`

**Instagram Console:**
- Redirect URIs: `http://your-app.elasticbeanstalk.com/login/oauth2/code/instagram`

### 8. Deploy

#### First Deployment (Manual)
```bash
# Build the application
./gradlew clean build -x test

# Create deployment package
mkdir -p deploy
cp build/libs/*.jar deploy/application.jar
cp -r .ebextensions deploy/
cd deploy
zip -r ../deploy.zip .
cd ..

# Deploy using AWS CLI
aws elasticbeanstalk create-application-version \
  --application-name am-i-detained \
  --version-label v1 \
  --source-bundle S3Bucket=elasticbeanstalk-us-east-1-512978621387,S3Key=deploy.zip \
  --region us-east-1

aws elasticbeanstalk update-environment \
  --environment-name am-i-detained-prod \
  --version-label v1 \
  --region us-east-1
```

#### Automatic Deployment (GitHub Actions)
1. Merge a PR to `main` branch
2. GitHub Actions will automatically:
   - Build the application
   - Run tests
   - Package the JAR
   - Deploy to Elastic Beanstalk
3. Monitor deployment in Actions tab

## Monitoring

### View Application Logs
```bash
# Using AWS CLI
aws elasticbeanstalk retrieve-environment-info \
  --environment-name am-i-detained-prod \
  --info-type tail \
  --region us-east-1

# Or via Console
# Go to Environment → Logs → Request Logs → Last 100 Lines
```

### Health Monitoring
- Go to Environment → Monitoring
- View metrics: CPU, Memory, Requests, Response time
- Set up CloudWatch alarms for critical metrics

## Cost Optimization

### Free Tier (First 12 Months)
- 750 hours/month EC2 t2.micro
- 750 hours/month RDS db.t2.micro
- **Estimated cost: $0/month**

### After Free Tier
- EC2 t3.micro: ~$7.50/month
- RDS db.t3.micro: ~$15/month
- **Estimated cost: ~$22/month**

### Cost Reduction Tips
1. Use Single Instance (not Load Balanced) for low traffic
2. Stop environment when not in use:
   ```bash
   aws elasticbeanstalk terminate-environment \
     --environment-name am-i-detained-prod
   ```
3. Use RDS snapshots and restore when needed
4. Consider Aurora Serverless for database (pay per use)

## Troubleshooting

### Deployment Failed
1. Check GitHub Actions logs
2. Check Elastic Beanstalk events:
   ```bash
   aws elasticbeanstalk describe-events \
     --environment-name am-i-detained-prod \
     --max-records 20
   ```

### Application Not Starting
1. Check logs in Elastic Beanstalk console
2. Verify environment variables are set correctly
3. Ensure database is accessible from EC2 instance
4. Check security group rules

### Database Connection Issues
1. Verify RDS security group allows inbound from EC2 security group
2. Check DATABASE_URL format
3. Verify database credentials
4. Test connection from EC2 instance:
   ```bash
   # SSH into instance
   eb ssh am-i-detained-prod
   
   # Test database connection
   psql -h your-rds-endpoint -U postgres -d am_i_detained
   ```

## Updating the Application

### Via GitHub Actions (Recommended)
1. Make changes to code
2. Create PR and merge to `main`
3. Automatic deployment triggers

### Manual Update
```bash
./gradlew build -x test
# Create deploy.zip as shown above
# Upload via Elastic Beanstalk console or CLI
```

## Rollback

### Via Console
1. Go to Environment → Application versions
2. Select previous version
3. Click "Deploy"

### Via CLI
```bash
aws elasticbeanstalk update-environment \
  --environment-name am-i-detained-prod \
  --version-label previous-version-label \
  --region us-east-1
```

## Security Best Practices

1. **Never commit secrets** to `.ebextensions` files
2. Use **AWS Secrets Manager** for production secrets
3. Enable **HTTPS** with ACM certificate
4. Restrict **security groups** to necessary ports only
5. Enable **RDS encryption** at rest
6. Use **IAM roles** instead of access keys when possible
7. Enable **CloudTrail** for audit logging

## Next Steps

1. Set up custom domain with Route 53
2. Add HTTPS with AWS Certificate Manager
3. Configure CloudWatch alarms
4. Set up automated backups for RDS
5. Implement blue/green deployments
6. Add staging environment
