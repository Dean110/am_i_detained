#!/bin/bash
# Setup script for AWS Elastic Beanstalk
# Run this once to create your application and environment

set -e

# Configuration
APP_NAME="am-i-detained"
ENV_NAME="am-i-detained-prod"
REGION="us-east-1"
INSTANCE_TYPE="t3.micro"
SOLUTION_STACK="64bit Amazon Linux 2023 v5.9.2 running Tomcat 11 Corretto 25"

echo "🚀 Creating Elastic Beanstalk Application and Environment"
echo "=========================================================="
echo ""

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "❌ AWS CLI not found. Please install it first:"
    echo "   macOS: brew install awscli"
    echo "   Linux: pip install awscli"
    exit 1
fi

# Check if AWS credentials are configured
if ! aws sts get-caller-identity &> /dev/null; then
    echo "❌ AWS credentials not configured. Run: aws configure"
    exit 1
fi

echo "✅ AWS CLI configured"
echo ""

# Use hardcoded solution stack for Java 25
PLATFORM="$SOLUTION_STACK"
echo "✅ Using platform: $PLATFORM"
echo ""

# Create application
echo "📦 Creating Elastic Beanstalk application: $APP_NAME"
APP_EXISTS=$(aws elasticbeanstalk describe-applications \
    --application-names $APP_NAME \
    --region $REGION \
    --query 'Applications[0].ApplicationName' \
    --output text 2>/dev/null)

if [ "$APP_EXISTS" = "$APP_NAME" ]; then
    echo "⚠️  Application already exists, skipping..."
else
    aws elasticbeanstalk create-application \
        --application-name $APP_NAME \
        --description "Check-in alert system" \
        --region $REGION
    echo "✅ Application created"
fi
echo ""

# Create environment
echo "🌍 Creating environment: $ENV_NAME"
if aws elasticbeanstalk describe-environments \
    --environment-names $ENV_NAME \
    --region $REGION \
    --query 'Environments[0].Status' \
    --output text 2>/dev/null | grep -q "Ready\|Launching\|Updating"; then
    echo "⚠️  Environment already exists, skipping..."
else
    aws elasticbeanstalk create-environment \
        --application-name $APP_NAME \
        --environment-name $ENV_NAME \
        --solution-stack-name "$SOLUTION_STACK" \
        --option-settings \
            Namespace=aws:autoscaling:launchconfiguration,OptionName=InstanceType,Value=$INSTANCE_TYPE \
            Namespace=aws:elasticbeanstalk:environment,OptionName=EnvironmentType,Value=SingleInstance \
            Namespace=aws:elasticbeanstalk:environment:proxy,OptionName=ProxyServer,Value=nginx \
            Namespace=aws:elasticbeanstalk:application:environment,OptionName=SERVER_PORT,Value=5000 \
            Namespace=aws:elasticbeanstalk:application:environment,OptionName=SPRING_PROFILES_ACTIVE,Value=prod \
        --region $REGION
    
    echo "✅ Environment creation started (this takes 5-10 minutes)"
    echo ""
    echo "⏳ Waiting for environment to be ready..."
    
    aws elasticbeanstalk wait environment-exists \
        --environment-names $ENV_NAME \
        --region $REGION
    
    # Wait for environment to be ready
    while true; do
        STATUS=$(aws elasticbeanstalk describe-environments \
            --environment-names $ENV_NAME \
            --region $REGION \
            --query 'Environments[0].Status' \
            --output text)
        
        HEALTH=$(aws elasticbeanstalk describe-environments \
            --environment-names $ENV_NAME \
            --region $REGION \
            --query 'Environments[0].Health' \
            --output text)
        
        echo "   Status: $STATUS | Health: $HEALTH"
        
        if [ "$STATUS" = "Ready" ]; then
            break
        fi
        
        if [ "$STATUS" = "Terminated" ]; then
            echo "❌ Environment creation failed"
            exit 1
        fi
        
        sleep 30
    done
fi

echo ""
echo "✅ Environment is ready!"
echo ""

# Get environment URL
ENV_URL=$(aws elasticbeanstalk describe-environments \
    --environment-names $ENV_NAME \
    --region $REGION \
    --query 'Environments[0].CNAME' \
    --output text)

echo "=========================================================="
echo "🎉 Setup Complete!"
echo "=========================================================="
echo ""
echo "Application Name: $APP_NAME"
echo "Environment Name: $ENV_NAME"
echo "Region: $REGION"
echo "URL: http://$ENV_URL"
echo ""
echo "📝 Next Steps:"
echo "1. Update OAuth redirect URIs with: http://$ENV_URL"
echo "2. Set environment variables in AWS Console:"
echo "   - Go to: https://console.aws.amazon.com/elasticbeanstalk"
echo "   - Select: $ENV_NAME"
echo "   - Configuration → Software → Environment properties"
echo "   - Add: GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, etc."
echo ""
echo "3. Add GitHub secrets:"
echo "   - AWS_ACCESS_KEY_ID"
echo "   - AWS_SECRET_ACCESS_KEY"
echo ""
echo "4. Deploy by merging a PR to main branch!"
echo ""
